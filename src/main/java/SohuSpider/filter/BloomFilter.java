package main.java.SohuSpider.filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.BitSet;
import java.util.concurrent.BlockingQueue;


/*
 * Bloom Filter算法 高效去重策略
 */

public class BloomFilter implements Serializable{
	
	/* BitSet初始分配空间大小 2^24 */
	private static final int DEFAULT_SIZE = 1 << 25;
	
	/* 不同哈希函数种子，一般应取质数 */
	private static final int[] seeds = new int[]{5,7,11,13,31,37,61};
	
	private BitSet bits = null;
	
	/* 哈希函数对象 */
	private SimpleHash[] func = new SimpleHash[seeds.length];
	
	public BloomFilter(){
		for(int i  = 0; i < seeds.length; i++){
			func[i] = new SimpleHash(DEFAULT_SIZE,seeds[i]);
		}
		
		File filterSer = new File("bits.ser");
		if (filterSer.exists()) {
			try{
				  //对象反序列化
				  ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filterSer));
				  bits = (BitSet) ois.readObject();
				ois.close();
			} catch (Exception e) {
			   e.printStackTrace();
		    }  
		}else{
			bits = new BitSet(DEFAULT_SIZE);
		}
		
	}
	
	//将字符串映射到bits中
	public synchronized/*同步锁标记*/ void  add(String value){
		for(SimpleHash f : func){
			bits.set(f.hash(value),true);
		}
	}
	
	public BitSet getBitset(){
		return bits;
	}
	
	//判断字符串是否已存在于bits集合中
	public boolean contains(String value){
		if(value == null)
			return false;
		
		boolean ret = true;
		for(SimpleHash f : func){
			ret = ret && bits.get(f.hash(value)); //当前仅当所有哈希函数计算出的标志位都为1的时候确定字符串一定在集合中
		}
		
		return ret;
	}
	
	/*哈希函数类*/
	public static class SimpleHash {
		private int cap;
		private int seed;
		
		public SimpleHash(int cap, int seed){
			this.cap = cap;
			this.seed = seed;
		}
		
		//hash函数，采用简单的加权和hash
		public int hash(String value){
			int result = 0 ;
			int len = value.length();
			for(int i = 0; i < len; i++){
				result = seed * result + value.charAt(i);
			}
			return (cap - 1) & result;
		}
	}
	
}
