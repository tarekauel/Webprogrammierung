package WikiCrawler;

import java.net.URL;

public class Page implements Comparable<Page>{
	
	private URL url; 
	private Page parent;
	
	public Page(URL url) {
		this(url, null);		
	}
	
	public Page(URL url, Page parent) {
		this.url = url;
		this.parent = parent;
	}
	
	public boolean equals(Page p) {
		return p.url.equals(url);
	}
	
	public URL getURL() {
		return url;
	}
	
	public Page getParent() {
		return parent;
	}

	@Override
	public int compareTo(Page p) {
		String pPath = p.getURL().getPath();
		String xPath = url.getPath();
		return pPath.compareToIgnoreCase(xPath);
	}
}
