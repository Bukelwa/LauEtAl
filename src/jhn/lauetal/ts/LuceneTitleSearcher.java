package jhn.lauetal.ts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

import jhn.wp.Fields;

public class LuceneTitleSearcher implements OrderedTitleSearcher {
	private static final Version luceneVersion = Version.LUCENE_36;
	
	private IndexSearcher s;
	private QueryParser qp;
	private final int n;
	
	public LuceneTitleSearcher(String topicWordIdxDir, int topN) throws CorruptIndexException, IOException {
		this(IndexReader.open(NIOFSDirectory.open(new File(topicWordIdxDir))), topN);
	}
	
	public LuceneTitleSearcher(IndexReader topicWordIdx, int topN) throws CorruptIndexException, IOException {
		s = new IndexSearcher(topicWordIdx);
		
		Analyzer a = new StandardAnalyzer(luceneVersion);
		qp = new QueryParser(luceneVersion, Fields.text, a);
		
		this.n = topN;
	}
	
	
	
	//FIXME Learn to do without the QueryParser instance
	public List<String> titles(String... terms) throws Exception {
		StringBuilder searchString = new StringBuilder();
		for(String term : terms) {
			searchString.append(' ');
			searchString.append(term);
		}
		Query q = qp.parse(searchString.toString());
		
		TopDocs tds = s.search(q, n);
		List<String> titles = new ArrayList<String>();
		for(ScoreDoc sd : tds.scoreDocs) {
			titles.add(s.doc(sd.doc).get(Fields.label));
		}
		
		return titles;
	}

}
