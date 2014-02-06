package com.engagepoint.university.admincentre.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

import com.engagepoint.university.admincentre.entity.AbstractEntity;
import com.engagepoint.university.admincentre.entity.Node;
import com.engagepoint.university.admincentre.synchronization.CRUDObserver;
import com.engagepoint.university.admincentre.synchronization.CRUDOperation;
import com.engagepoint.university.admincentre.synchronization.MessagePayload;

public abstract class AbstractDAO<T extends AbstractEntity>
	extends Observable
	implements GenericDAO<T> {

    private static String CACHE_CONFIG = "infinispan/cache_config.xml";
    private static String USED_CACHE = "evictionCache";

    DefaultCacheManager m = null;
    Cache<String, T> cache = null;
    private Class<T> type;

    public AbstractDAO(Class<T> type) {
        this.type = type;
        addObserver(new CRUDObserver());
    }

    public void create(T newInstance) throws Exception {
        try {
            Cache<String, T> cache = getCache(CACHE_CONFIG, USED_CACHE);

            if (!cache.containsKey(newInstance.getId())) {
                cache.put(newInstance.getId(), newInstance);
                setChanged();
                notifyObservers(new MessagePayload(
                		CRUDOperation.CREATE, newInstance));
            } else {
                throw new Exception("This entity already exists");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
 finally {
            stopCacheManager();
        }

    }

    public T read(String id) throws IOException {
        try {
            T variable = null;
            Cache<String, T> cache = getCache(CACHE_CONFIG, USED_CACHE);
            if (cache.containsKey(id)) {
                variable = cache.get(id);
            }
            return variable;
        } finally {
            stopCacheManager();
        }
    }

    public void update(T transientObject) throws Exception {
        try {
        Cache<String, T> cache = getCache(CACHE_CONFIG, USED_CACHE);
        cache.replace(transientObject.getId(), transientObject);
        setChanged();
        notifyObservers(new MessagePayload(
        		CRUDOperation.UPDATE, transientObject));
        } finally {
            stopCacheManager();
        }

    }

    public void delete(T persistentObject) throws Exception {
        try {
            Cache<String, T> cache = getCache(CACHE_CONFIG, USED_CACHE);
            if (cache.containsKey(persistentObject.getId())
                    && (!persistentObject.getId().equals("/root"))) {
                cache.replace(persistentObject.getId(), persistentObject);
                setChanged();
                notifyObservers(new MessagePayload(
                		CRUDOperation.DELETE, persistentObject));
            } else {
                throw new Exception("This entity doesn`t exist");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
 finally {
            stopCacheManager();
        }

    }

    private Cache<String, T> getCache(String cacheConfigPath, String cacheName) throws IOException {

        m = new DefaultCacheManager(cacheConfigPath);
        cache = m.getCache(cacheName);
        if (!cache.containsKey("/root")) {
            Cache<String, Node> startCache = m.getCache(USED_CACHE);
            Node node = new Node();
            node.setName("root");
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
}
