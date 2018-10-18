package com.xsfdev.componentmvp;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by xsf on 2018/10/18.
 * Description:
 */
final public class IndexAllocator<K> {
    private WeakHashMap<K, Integer> mIndexes = new WeakHashMap<>();

    /**
     * 为child分配一个index
     * 如果之前已经分配过,使用之前的index
     *
     * @param obj
     * @return
     */
    public synchronized int allocateIndex(K obj, int from, int to) {
        if (obj == null) {
            return -1;
        }

        if (mIndexes.containsKey(obj)) {
            Integer value = mIndexes.get(obj);
            if (value != null) {
                return value;
            } else {
                mIndexes.remove(obj);
            }
        }

        for (int i = from; i < to; i++) {
            if (mIndexes.containsValue(i)) {
                continue;
            }
            mIndexes.put(obj, i);
            return i;
        }
        return -1;
    }

    /**
     * 通过index找到一个child
     * 如果之前没有为child分配过信息,返回null
     *
     * @param index
     * @return
     */
    public synchronized K findByIndex(int index) {
        if (!mIndexes.containsValue(index)) {
            return null;
        }

        for (Map.Entry<K, Integer> entry : mIndexes.entrySet()) {
            Integer value = entry.getValue();
            if (value == null) {
                continue;
            }
            if (value == index) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 将一个已经分配的index删除
     *
     * @param obj
     */
    public synchronized void removeIndex(K obj) {
        if (obj == null) {
            return;
        }
        mIndexes.remove(obj);
    }
}
