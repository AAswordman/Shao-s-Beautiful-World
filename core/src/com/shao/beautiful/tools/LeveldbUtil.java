package com.shao.beautiful.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.print.attribute.standard.Compression;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;


public class LeveldbUtil {
	private DB db = null;
	private String charset = "utf-8";
	private File f;
	public LeveldbUtil(File f) {
		this.f=f;
		initLevelDB();
	}
	/**
	 * 初始化LevelDB 每次使用levelDB前都要调用此方法，无论db是否存在
	 */
	public void initLevelDB() {
		DBFactory factory = new Iq80DBFactory();
		Options options = new Options();
		options.createIfMissing(true);
		try {
			this.db = factory.open(f, options);
		} catch (IOException e) {
			System.out.println("levelDB启动异常");
			e.printStackTrace();
		}
	}

	/**
	 * 存放数据
	 *
	 * @param key
	 * @param val
	 */
	public void put(byte[] key, byte[] val) {
		
		this.db.put(key, compress(val));
	}

	private byte[] compress(byte[] val) {
		ByteArrayOutputStream arrayOutputStream=new ByteArrayOutputStream();
		GZIPOutputStream gzipOutputStream;
		try {
			gzipOutputStream=new GZIPOutputStream(arrayOutputStream);
			gzipOutputStream.write(val);
			gzipOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return arrayOutputStream.toByteArray();
	}
	
	/**
	 * 根据key获取数据
	 *
	 * @param key
	 * @return
	 */
	public byte[] get(byte[] key) {
		byte[] val = null;
	    val = db.get(key);
		
		if (val == null) {
			return null;
		}
		
		return unCompress(val);
	}

	private byte[] unCompress(byte[] val) {
		ByteArrayInputStream arrayInputStream=new ByteArrayInputStream(val);
		ByteArrayOutputStream arrayOutputStream=new ByteArrayOutputStream();
		GZIPInputStream gzipInputStream;
		byte[] buffer=new byte[2048];
		int n;
		try {
			gzipInputStream=new GZIPInputStream(arrayInputStream);
			while ((n=gzipInputStream.read(buffer))>=0) {
				arrayOutputStream.write(buffer,0,n);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return arrayOutputStream.toByteArray();
	}
	/**
	 * 根据key删除数据
	 *
	 * @param key
	 */
	public void delete(String key) {
		try {
			db.delete(key.getBytes(charset));
		} catch (Exception e) {
			System.out.println("levelDB delete error");
			e.printStackTrace();
		}
	}

	/**
	 * 关闭数据库连接 每次只要调用了initDB方法，就要在最后调用此方法
	 */
	public void close() {
		if (db != null) {
			try {
				db.close();
			} catch (IOException e) {
				System.out.println("levelDB 关闭异常");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取所有key
	 *
	 * @return
	
	public List<String> getKeys() {

		List<String> list = new ArrayList<>();
		DBIterator iterator = null;
		try {
			iterator = db.iterator();
			while (iterator.hasNext()) {
				Map.Entry<byte[], byte[]> item = iterator.next();
				String key = new String(item.getKey(), charset);
				list.add(key);
			}
		} catch (Exception e) {
			System.out.println("遍历发生异常");
			e.printStackTrace();
		} finally {
			if (iterator != null) {
				try {
					iterator.close();
				} catch (IOException e) {
					System.out.println("遍历发生异常");
					e.printStackTrace();
				}

			}
		}
		return list;
	}
	*/
	
}