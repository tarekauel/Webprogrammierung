package WikiCrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.TreeSet;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

public class Crawler extends HTMLEditorKit.ParserCallback implements Runnable {

	private static String			root		= "http://de.wikipedia.org";

	private static String			finish;

	private Page					currentPage;

	private static Queue<Page>		workQueue	= new LinkedList<Page>();

	private static TreeSet<Page>	closed		= new TreeSet<Page>();

	private static int				elements	= 0;

	private static Page				finishPage	= null;

	private static boolean			output		= false;

	private static long				startTime;

	private boolean					wait;

	public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
		if (t != HTML.Tag.A || finishPage != null)
			return;

		String href = (String) a.getAttribute(HTML.Attribute.HREF);

		if (href == null || !href.startsWith("/wiki/") || href.contains(":"))
			return;

		try {
			href = URLDecoder.decode(href, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		try {
			Page p = new Page(new URL(root + href), currentPage);
			if (href.equals(finish)) {
				finishPage = p;
			}
			addPage(p);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private synchronized boolean addPage(Page p) {
		boolean res = closed.add(p);
		if (res) {
			workQueue.add(p);
			++elements;
			System.out.println(p.getURL().getPath());
			return true;
		}
		return false;
	}

	private synchronized Page getElement() {
		try {
			return workQueue.remove();
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	public static void main(String[] args) throws IOException {
		BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Start-Begriff: ");
		String start = sysIn.readLine();
		System.out.print("Ziel-Begriff: ");
		String ziel = sysIn.readLine();

		new Crawler("/wiki/" + start, "/wiki/" + ziel);
	}

	public Crawler(String start, String finish) throws IOException {
		startTime = System.currentTimeMillis();
		this.finish = finish;
		this.currentPage = new Page(new URL(root + start));
		closed.add(this.currentPage);
		crawlPage(this.currentPage);
		new Thread(new Crawler(false)).start();
		for (int i = 0; i < 3; i++)
			new Thread(new Crawler(true)).start();
	}

	public Crawler(boolean wait) {
		this.wait = wait;
	}

	private void crawlPage(Page p) throws IOException {
		this.currentPage = p;
		URLConnection con = p.getURL().openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		new ParserDelegator().parse(in, this, false);
	}

	private synchronized void ausgabe(Page p) {
		if (output)
			return;
		if (p == null) {
			System.out.println("Kein Weg Gefunden =(, nichts mehr zu tun");
		} else {
			System.out.print(">");
			String ausgabe = "";
			ausgabe += p.getURL().getPath().substring(6);
			Page parent = p.getParent();
			while (parent != null) {
				ausgabe = parent.getURL().getPath().substring(6) + "-->" + ausgabe;
				parent = parent.getParent();
			}
			System.out.println(ausgabe);
		}
		System.out.println("");
		System.out.println("Es wurden " + elements + " Elemente aufgenommen!");
		long time = System.currentTimeMillis() - startTime;
		long hours = time / (1000 * 60 * 60);
		long minutes = time / (1000 * 60);
		long seconds = time / (1000);
		long milliseconds = time % 1000;
		System.out.println("Ausführzeit: " + hours + "H " + minutes + "M " + seconds + "S " + milliseconds + "Ms");
		output = true;
	}

	@Override
	public void run() {

			if (wait) {
				try {
					wait((long) Math.floor(Math.random() * 10000));
				} catch (InterruptedException e) {
				}
			}
			try {
				Page p = getElement();
				while (p != null && finishPage == null) {
					try {
					crawlPage(p);
					} catch (Exception e) {
						e.printStackTrace();
					}
					p = getElement();
				}
				ausgabe(finishPage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		

	}

}
