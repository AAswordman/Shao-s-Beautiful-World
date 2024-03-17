package com.shao.beautiful.tools;

import java.io.Serializable;
import java.util.Hashtable;


public class LruCache<K, V> implements Serializable{
	private static final long serialVersionUID = 54424752L;
	protected int cacheSize;// 缓存大小
	private Hashtable<K, CacheNode> nodes;// 缓存容器
	private int currentSize;// 当前缓存对象数量
	private CacheNode first;// (实现双链表)链表头
	private CacheNode last;// (实现双链表)链表尾

	class CacheNode {
		CacheNode prev;// 前一节点
		CacheNode next;// 后一节点
		V value;// 值
		K key;// 键

		CacheNode() {
		}
	}

	public int size() {
		return currentSize;
	}

	public LruCache(int i) {
		currentSize = 0;
		cacheSize = i;
		nodes = new Hashtable<K, CacheNode>(i);// 缓存容器
	}

	/**
	 * 获取缓存中对象
	 *
	 * @param key
	 * @return
	 */
	public synchronized V get(K key) {
		CacheNode node = (CacheNode) nodes.get(key);
		if (node != null) {
			moveToHead(node);
			return node.value;
		} else {
			return null;
		}
	}

	/**
	 * 添加缓存
	 *
	 * @param key
	 * @param value
	 */
	public synchronized void put(K key, V value) {
		CacheNode node = (CacheNode) nodes.get(key);
		
		if (node == null) {
			// 缓存容器是否已经超过大小.
			if (currentSize >= cacheSize) {
				if (last != null)// 将最少使用的删除
					nodes.remove(last.key);
				removeLast();
			} else {
				currentSize++;
			}

			node = new CacheNode();
		}
		node.value = value;
		node.key = key;
		// 将最新使用的节点放到链表头，表示最新使用的.
		moveToHead(node);
		nodes.put(key, node);
	}

	/**
	 * 将缓存删除
	 *
	 * @param key
	 * @return
	 */
	public synchronized V remove(K key) {
		CacheNode node = (CacheNode) nodes.get(key);
		if (node != null) {
			if (node.prev != null) {
				node.prev.next = node.next;
			}
			if (node.next != null) {
				node.next.prev = node.prev;
			}
			if (last == node)
				last = node.prev;
			if (first == node)
				first = node.next;
		}
		return node.value;
	}

	public synchronized void clear() {
		first = null;
		last = null;
	}

	/**
	 * 删除链表尾部节点 表示 删除最少使用的缓存对象
	 */
	private synchronized void removeLast() {
		// 链表尾不为空,则将链表尾指向null. 删除连表尾（删除最少使用的缓存对象）
		if (last != null) {
			if (last.prev != null) {
				if (last.prev.next != null) {
					removeEldest(last.prev.next.value);
				}
				last.prev.next = null;
			} else {
				removeEldest(first.value);
				first = null;
			}
			last = last.prev;
		}
	}

	protected void removeEldest(V value) {
	}

	/**
	 * 移动到链表头，表示这个节点是最新使用过的
	 *
	 * @param node
	 */
	private synchronized void moveToHead(CacheNode node) {
		if (node == first)
			return;
		if (node.prev != null)
			node.prev.next = node.next;
		if (node.next != null)
			node.next.prev = node.prev;
		if (last == node)
			last = node.prev;
		if (first != null) {
			node.next = first;
			first.prev = node;
		}
		first = node;
		node.prev = null;
		if (last == null)
			last = first;
	}

	public boolean containsKey(K key) {
		return nodes.get(key) != null;
	}
}