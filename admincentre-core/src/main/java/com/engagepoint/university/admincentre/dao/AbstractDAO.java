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

import com.engagepoint.university.admincentre.entity.AbstractEntity;
import com.engagepoint.university.admincentre.entity.Node;
import com.engagepoint.university.admincentre.synchronization.CRUDObserver;
import com.engagepoint.university.admincentre.synchronization.CRUDOperation;
import com.engagepoint.university.admincentre.synchronization.MessagePayload;

public abstract class AbstractDAO<T extends AbstractEntity>
	extends Observable
	implements GenericDAO<T> {

    private static final String CACHE_CONFIG = "cache_config.xml";
    private static final String USED_CACHE = "evictionCache";

    private DefaultCacheManager m = null;
    private Cache<String, T> cache = null;
    private Class<T> type;

    public AbstractDAO(Class<T> type) {
        this.type = type;
        addObserver(new CRUDObserver());
    }

    public void create(T newInstance) throws IOException {
        try {
            getCache(CACHE_CONFIG, USED_CACHE);

            if (!cache.containsKey(newInstance.getId())) {
                cache.put(newInstance.getId(), newInstance);
                setChanged();
                notifyObservers(new MessagePayload(
                		CRUDOperation.CREATE, newInstance));
            } else {
                throw new Exception("This entity already exists");
            }
        } catch (Exception e) {
            throw new IOException("This entity already exists");
        }
 finally {
            stopCacheManager();
        }

    }

    public T read(String id) throws IOException {
        try {
            T variable = null;
            getCache(CACHE_CONFIG, USED_CACHE);
            if (cache.containsKey(id)) {
                variable = cache.get(id);
            }
            setChanged();
            notifyObservers(new MessagePayload(
            		CRUDOperation.READ, variable));
            return variable;
        } finally {
            stopCacheManager();
        }
    }

    public void update(T transientObject) throws IOException {
        try {
            getCache(CACHE_CONFIG, USED_CACHE);
        cache.replace(transientObject.getId(), transientObject);
        setChanged();
        notifyObservers(new MessagePayload(
        		CRUDOperation.UPDATE, transientObject));
        } finally {
            stopCacheManager();
        }

    }

    public void delete(String keyId) throws IOException{
        try {
            getCache(CACHE_CONFIG, USED_CACHE);
            T temp = cache.get(keyId);
            if (temp == null) {
                throw new IOException();
            }
            cache.remove(keyId);
                setChanged();
                notifyObservers(new MessagePayload(
                		CRUDOperation.DELETE, temp));

            }

 finally {
            stopCacheManager();
        }

    }

    public List<T> search(String name) throws IOException {
        try {
            getCache(CACHE_CONFIG, USED_CACHE);
            SearchManager searchManager = Search.getSearchManager(cache);
            QueryFactory qf = searchManager.getQueryFactory();
            Query query = qf.from(type).having("name").like("*" + name + "*").toBuilder().build();
            List<T> resultList = query.list();
            if (resultList.size() != 0) {
                return resultList;
            }
            return new LinkedList<T>();
        }

        finally {
            stopCacheManager();
        }
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
    public void putAll(Map<String, T> cacheData){
    	try {
            getCache(CACHE_CONFIG, USED_CACHE);
			cache.putAll(cacheData);
		} catch (IOException e) {
			throw new UnsupportedOperationException("Could not put all");
		} finally {
			stopCacheManager();
		}
    }
    
    /**
     * Clears cache
     * @throws UnsupportedOperationException if cache could not be clear
     */
    public void clear(){
    	try {
            getCache(CACHE_CONFIG, USED_CACHE);
			cache.clear();
		} catch (IOException e) {
			throw new UnsupportedOperationException("Cache could not be clear");
		} finally {
			stopCacheManager();
		}
    }
}