package com.engagepoint.university.admincentre.dao;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.query.Search;
import org.infinispan.query.SearchManager;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engagepoint.university.admincentre.entity.AbstractEntity;
import com.engagepoint.university.admincentre.entity.Node;
import com.engagepoint.university.admincentre.preferences.NodePreferences;
import com.engagepoint.university.admincentre.synchronization.CRUDObserver;
import com.engagepoint.university.admincentre.synchronization.CRUDOperation;
import com.engagepoint.university.admincentre.synchronization.CRUDPayload;

public abstract class AbstractDAO<T extends AbstractEntity>
	extends Observable
	implements GenericDAO<T> {

    private static final String CACHE_CONFIG = "cache_config.xml";
    private static final String USED_CACHE = "evictionCache";
    private static final Logger LOGGER = LoggerFactory.getLogger(NodePreferences.class);

    private DefaultCacheManager m = null;
    private Cache<String, T> cache = null;
    private Class<T> type;

    public AbstractDAO(Class<T> type) {
        this.type = type;
        addObserver(new CRUDObserver());
        try {
            getCache(CACHE_CONFIG, USED_CACHE);
        } catch (IOException e) {
            LOGGER.warn("couldn`t start the cache" + USED_CACHE);
        }
    }

    public void create(T newInstance) throws IOException {

            String instanceId = newInstance.getId();
            if (!cache.containsKey(instanceId)) {
                cache.put(instanceId, newInstance);
                setChanged();
                notifyObservers(new CRUDPayload(CRUDOperation.CREATE, newInstance));
            } else {
                throw new IOException("This entity already exists"+newInstance.getName());
            }


    }

    public T read(String id) throws IOException {

            T variable = null;

            if (cache.containsKey(id)) {
                variable = cache.get(id);
            }
            setChanged();
            notifyObservers(new CRUDPayload(CRUDOperation.READ, variable));
            return variable;

    }

    public void update(T transientObject) throws IOException {

        cache.replace(transientObject.getId(), transientObject);
        setChanged();
        notifyObservers(new CRUDPayload(CRUDOperation.UPDATE, transientObject));


    }

    public void delete(String keyId) throws IOException{

            T temp = cache.get(keyId);
            if (temp == null) {
                throw new IOException();
            }
            cache.remove(keyId);
                setChanged();
                notifyObservers(new CRUDPayload(CRUDOperation.DELETE, temp));



    }

    public List<T> search(String name) throws IOException {

            SearchManager searchManager = Search.getSearchManager(cache);
            QueryFactory qf = searchManager.getQueryFactory();
            Query query = qf.from(type).having("name").like("*" + name + "*").toBuilder().build();
            List<T> resultList = query.list();
            if (!resultList.isEmpty()) {
                return resultList;
            }
            return new LinkedList<T>();

    }
    private Cache<String, T> getCache(String cacheConfigPath, String cacheName) throws IOException {

        m = new DefaultCacheManager(cacheConfigPath);
        cache = m.getCache(cacheName);
        if (!cache.containsKey("/")) {
            Cache<String, Node> startCache = m.getCache(USED_CACHE);
            Node node = new Node("/", "");
            startCache.put(node.getId(), node);
        }
        return cache;

    }

    private void stopCacheManager() {
        if (m != null) {
            cache.stop();
            m.stop();
        }
    }
    
    /**
     * Puts new received cache
     * @throws UnsupportedOperationException if could not put all
     */
    public void putAll(Map<String, T> cacheData) throws IOException {

			cache.putAll(cacheData);


    }

    /**
     * Clears cache
     * @throws UnsupportedOperationException if cache could not be clear
     */
    public void clear() throws IOException {

			cache.clear();

    }
    
    public Map<String, T> obtainCache() throws IOException{
    	return getCache(CACHE_CONFIG, USED_CACHE);
    }
    
    @Override
    public void finalize() {
        stopCacheManager();
    }
}
