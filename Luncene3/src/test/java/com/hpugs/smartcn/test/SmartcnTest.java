package com.hpugs.smartcn.test;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @Description 中文分词器使用
 * @author 高尚
 * @version 1.0
 * @date 创建时间：2018年1月25日 下午4:14:09
 */
public class SmartcnTest {
	
	private static final Integer[] ids = {1, 2, 3};
	private static final String[] citys = {"北京", "郑州", "杭州"};
	private static final String[] descs = {"北京是国家首都", "郑州是我的故乡非常漂亮", "杭州是一个漂亮的城市"};
	
	private static final String dir = "F:\\luncene\\index";
	
	private static IndexWriter indexWriter;
	private static IndexReader indexReader;
	
	@Before
	public void initIndexReader(){
		try {
			//初始化写索引
			Directory directory = FSDirectory.open(Paths.get(dir));
//			Analyzer analyzer = new StandardAnalyzer();//标准分词器
			Analyzer analyzer = new SmartChineseAnalyzer();//中文分词器
			IndexWriterConfig conf = new IndexWriterConfig(analyzer);
			indexWriter = new IndexWriter(directory, conf);
			
			//初始化读索引
			indexReader = DirectoryReader.open(directory);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Description 写索引
	 *
	 * @author 高尚
	 * @version 1.0
	 * @date 创建时间：2018年1月24日 上午9:51:24
	 */
	@Test
	public void initIndexWriter(){
		try {
			//删除之前的索引
			indexWriter.deleteAll();
			indexWriter.forceMergeDeletes();
			indexWriter.commit();
			
			//得到document
			for (int i = 0; i < ids.length; i++) {
				Document document = new Document();
				document.add(new IntField("id", ids[i], Field.Store.YES));
				document.add(new StringField("city", citys[i], Field.Store.YES));
				document.add(new TextField("descs", descs[i], Field.Store.YES));
				indexWriter.addDocument(document);
			}
			
			System.out.println("写入文件：" + indexWriter.numDocs() + "个");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getIndexTest(){
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//		Analyzer analyzer = new StandardAnalyzer();//标准分词器
		Analyzer analyzer = new SmartChineseAnalyzer();//中文分词器
		QueryParser queryParser = new QueryParser("descs", analyzer);
		try {
			Query query = queryParser.parse("漂亮");
			TopDocs topDocs = indexSearcher.search(query, 10);
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document document = indexSearcher.doc(scoreDoc.doc);
				System.out.println("id:" + document.get("id") + ";city:" + document.get("city") + ";descs:" + document.get("descs"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void closeTest(){
		//关闭写索引
		if(null != indexWriter){
			try {
				indexWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//关闭读索引
		if(null != indexReader){
			try {
				indexReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
