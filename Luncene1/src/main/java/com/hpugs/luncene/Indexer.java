package com.hpugs.luncene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * @Description 实例化索引
 * @author 高尚
 * @version 1.0
 * @date 创建时间：2018年1月19日 下午5:21:22
 */
public class Indexer {

	private static IndexWriter indexWriter;
	private static IndexReader indexReader;
	
	/**
	 * @Description 初始化索引对象
	 * @param indexDir 待索引文件地址
	 *
	 * @author 高尚
	 * @version 1.0
	 * @date 创建时间：2018年1月19日 下午5:35:45
	 */
	public static IndexWriter initIndexWriter(String indexUri){
		try {
			Directory directory = FSDirectory.open(Paths.get(indexUri));
			Analyzer analyzer = new StandardAnalyzer();//标准分词器
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			indexWriter = new IndexWriter(directory, config);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return indexWriter;
	}
	
	/**
	 * @Description 关闭索引
	 *
	 * @author 高尚
	 * @version 1.0
	 * @date 创建时间：2018年1月19日 下午5:38:53
	 */
	public static void closeIndexWriter(){
		try {
			if(null != indexWriter){
				indexWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Description 对待分词文件进行分词处理
	 * @param dataUri 待分词文件地址
	 * @return 处理的分词文件数目
	 *
	 * @author 高尚
	 * @version 1.0
	 * @date 创建时间：2018年1月19日 下午5:44:04
	 */
	public static int index(String dataUri){
		File[] files = new File(dataUri).listFiles();
		for (File file : files) {
			indexFile(file);
		}
		return indexWriter.numDocs();
	}

	/**
	 * @Description 添加文件分词
	 * @param file
	 *
	 * @author 高尚
	 * @version 1.0
	 * @date 创建时间：2018年1月19日 下午6:03:36
	 */
	private static void indexFile(File file) {
		// TODO Auto-generated method stub
		Document document = getDocument(file);
		try {
			indexWriter.addDocument(document);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description 获得文件对象
	 * @param file
	 * @return
	 *
	 * @author 高尚
	 * @version 1.0
	 * @date 创建时间：2018年1月19日 下午6:03:58
	 */
	private static Document getDocument(File file) {
		// TODO Auto-generated method stub
		Document document = new Document();
		try {
			document.add(new TextField("contents", new FileReader(file)));
			document.add(new TextField("fileName", file.getName(), Field.Store.YES));
			document.add(new TextField("filePath", file.getCanonicalPath(), Field.Store.YES));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}
	
	/**
	 * @Description 读取分词数据
	 * @param indexDir 分词缓存地址
	 * @return
	 *
	 * @author 高尚
	 * @version 1.0
	 * @date 创建时间：2018年1月19日 下午6:28:00
	 */
	public static IndexReader initindexReader(String indexUri){
		try {
			Directory directory = FSDirectory.open(Paths.get(indexUri));
			indexReader = DirectoryReader.open(directory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return indexReader;
	}
	
	/**
	 * @Description 关闭读索引
	 *
	 * @author 高尚
	 * @version 1.0
	 * @date 创建时间：2018年1月19日 下午5:38:53
	 */
	public static void closeIndexReader(){
		try {
			if(null != indexReader){
				indexReader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Description 分词检索
	 *
	 * @author 高尚
	 * @version 1.0
	 * @throws ParseException 
	 * @date 创建时间：2018年1月19日 下午6:20:00
	 */
	public static void search(final String key){
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		Analyzer analyzer = new StandardAnalyzer();//标准分词器
		QueryParser queryParser = new QueryParser("contents", analyzer);
		try {
			Query query = queryParser.parse(key);
			TopDocs topDocs = indexSearcher.search(query, 10);
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document document = indexSearcher.doc(scoreDoc.doc);
				System.out.println(document.get("filePath"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
