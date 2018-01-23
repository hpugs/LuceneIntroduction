package com.hpugs.luncene.test;

import org.junit.After;
import org.junit.Test;

import com.hpugs.luncene.Indexer;

/**
 * @Description 分词测试
 * @author 高尚
 * @version 1.0
 * @date 创建时间：2018年1月19日 下午6:08:31
 */
public class IndexerWriterTest {
	
	//分词缓存地址
	private static final String indexUri = "F:\\luncene\\index";
	//分词文件地址
	private static final String dataUri = "F:\\luncene\\data";
	
	@Test
	public void indexTest(){
		Indexer.initIndexWriter(indexUri);
		int count = Indexer.index(dataUri);
		System.out.println("分词处理文件数量：" + count + "条");
	}
	
	@Test
	public void searchTest(){
		Indexer.initindexReader(indexUri);
		Indexer.search("Austrian");//取之Frederick the Great.txt
//		Indexer.search("a Austrian");
//		Indexer.search("is Austrian");
//		Indexer.search("a is Austrian");
	}
	
	@After
	public void closeTest(){
		Indexer.closeIndexWriter();
		Indexer.closeIndexReader();
	}

}
