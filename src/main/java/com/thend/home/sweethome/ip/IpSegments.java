package com.thend.home.sweethome.ip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpSegments {
	private static final Pattern LINE_NUMBER_PATTERN = Pattern
			.compile("lines\\s*=\\s*(\\d+)");
	private ArrayList<IpSegment> segments;

	public IpSegments() {
		this.segments = new ArrayList();
	}

	public void load(Reader in, boolean sort, boolean ignoreBadLine)
			throws IOException {
		BufferedReader reader = new BufferedReader(in);
		boolean inData = false;

		int lineNumber = 0;
		int lineCnt = 0;
		String line = null;

		while ((line = reader.readLine()) != null) {
			if (line.length() == 0)
				continue;
			++lineCnt;
			if (line.startsWith("#")) {
				if (inData)
					continue;
				Matcher matcher = LINE_NUMBER_PATTERN.matcher(line.substring(1)
						.trim());
				if (matcher.matches()) {
					lineNumber = Integer.parseInt(matcher.group(1));
				}

			}

			if (!(inData)) {
				this.segments.ensureCapacity(lineNumber);
				inData = true;
			}

			IpSegment seg = new IpSegment();
			try {
				seg.parse(line);
				this.segments.add(seg);
			} catch (ParseException e) {
				if (!(ignoreBadLine)) {
					throw new IOException("parse error : " + line);
				}
				System.out.println("bad line : " + line);
				e.printStackTrace();
			}
		}

		if ((lineNumber != 0) && (lineNumber > lineCnt) && (!(ignoreBadLine))) {
			throw new IOException("line cnt is too small! expected : "
					+ lineNumber + " but is " + lineCnt);
		}

		if (sort) {
			System.out.println("begin sorting segments");
			Collections.sort(this.segments);
		}
	}

	public void load(File file, boolean sort, boolean ignoreBadLine)
			throws IOException {
		load(new File[] { file }, sort, ignoreBadLine);
	}

	public void load(File[] files, boolean sort, boolean ignoreBadLine)
			throws IOException {
		for (File f : files) {
			InputStreamReader in = new InputStreamReader(
					new FileInputStream(f), "UTF-8");
			try {
				load(in, false, ignoreBadLine);
			} finally {
				in.close();
			}
		}

		if (sort)
			Collections.sort(this.segments);
	}

	public void save(Writer out) throws IOException {
		PrintWriter writer = new PrintWriter(out, true);
		writer.println("#lines=" + this.segments.size());
		for (IpSegment seg : this.segments) {
			writer.println(seg.formatLine());
		}
		writer.flush();
	}

	public void save(File file) throws IOException {
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(
				file), "UTF-8");
		try {
			save(out);
		} finally {
			out.close();
		}
	}

	public IpSegment locate(int ip) {
		IpSegment key = new IpSegment(ip, ip, null);
		int index = Collections.binarySearch(this.segments, key);
		if (index >= 0) {
			return ((IpSegment) this.segments.get(index));
		}
		index = -index - 1;
		IpSegment seg;
		if (index > 0) {
			seg = (IpSegment) this.segments.get(index - 1);
			if (ip <= seg.getTo()) {
				return seg;
			}
		}

		if (index < this.segments.size()) {
			seg = (IpSegment) this.segments.get(index);
			if (ip >= seg.getFrom()) {
				return seg;
			}
		}

		return null;
	}

	public List<IpSegment> getSegments() {
		return this.segments;
	}

	public IpSegment getSegment(int idx) {
		return ((IpSegment) this.segments.get(idx));
	}

	public void compact() {
		if (this.segments.isEmpty())
			return;

		ArrayList newSegments = new ArrayList(this.segments.size());

		int size = this.segments.size();
		IpSegment current = (IpSegment) this.segments.get(0);
		boolean copied = false;

		for (int i = 1; i < size; ++i) {
			IpSegment seg = (IpSegment) this.segments.get(i);
			if ((seg.getFrom() <= current.getTo() + 1)
					&& (seg.getFormatLocation().equals(current
							.getFormatLocation()))) {
				if (!(copied)) {
					IpSegment tmp = new IpSegment();
					tmp.copy(current);
					current = tmp;
					copied = true;
				}
				current.setTo(seg.getTo());
			} else {
				newSegments.add(current);
				current = seg;
				copied = false;
			}
		}
		newSegments.add(current);
		this.segments = newSegments;
	}

	public int[] getConflicts() {
		ArrayList conflicts = new ArrayList();
		int size = this.segments.size();
		for (int i = 1; i < size; ++i) {
			IpSegment prev = (IpSegment) this.segments.get(i - 1);
			IpSegment cur = (IpSegment) this.segments.get(i);
			if ((IpSegment.compareIp(cur.getFrom(), prev.getTo()) > 0)
					|| (cur.getDescription().equals(prev.getDescription())))
				continue;
			conflicts.add(Integer.valueOf(i));
		}

		int[] result = new int[conflicts.size()];
		int idx = 0;
		for (Iterator i$ = conflicts.iterator(); i$.hasNext();) {
			int c = ((Integer) i$.next()).intValue();
			result[(idx++)] = c;
		}
		return result;
	}

	public void removeOverlaps() {
		ArrayList list = new ArrayList(this.segments.size());

		int size = this.segments.size();
		int lastTo = 0;
		if (size > 0) {
			list.add(this.segments.get(0));
			lastTo = ((IpSegment) this.segments.get(0)).getTo();
		}

		for (int i = 1; i < size; ++i) {
			IpSegment seg = (IpSegment) this.segments.get(i);
			if (IpSegment.compareIp(seg.getFrom(), lastTo) <= 0) {
				if (IpSegment.compareIp(seg.getTo(), lastTo) <= 0) {
					continue;
				}

				seg.setFrom(IpSegment.nextIp(lastTo));
			}
			list.add(seg);
			lastTo = seg.getTo();
		}
		this.segments = list;
	}

	public void removeSmallSegments(int threshold) {
		ArrayList list = new ArrayList(this.segments.size());

		for (IpSegment seg : this.segments) {
			if (seg.size() >= threshold) {
				list.add(seg);
			}
		}

		this.segments = list;
	}

	public void cexpand() {
		int size = this.segments.size();
		IpSegment prevSeg = null;
		IpSegment currentSeg = null;
		IpSegment succSeg = (size > 0) ? (IpSegment) this.segments.get(0)
				: null;

		for (int i = 1; i <= size; ++i) {
			prevSeg = currentSeg;
			currentSeg = succSeg;
			succSeg = (i < size) ? (IpSegment) this.segments.get(i) : null;

			int probePrev = currentSeg.getFrom() & 0xFFFFFF00;
			if (probePrev < currentSeg.getFrom()) {
				if (prevSeg == null) {
					currentSeg.setFrom(probePrev);
				} else {
					if (probePrev <= prevSeg.getTo()) {
						probePrev = prevSeg.getTo() + 1;
					}
					if (probePrev < currentSeg.getFrom()) {
						currentSeg.setFrom(probePrev);
					}
				}
			}
			int probeSucc = currentSeg.getTo() | 0xFF;
			if (probeSucc > currentSeg.getTo())
				if (succSeg == null) {
					currentSeg.setTo(probeSucc);
				} else {
					if (probeSucc >= succSeg.getFrom()) {
						probeSucc = succSeg.getFrom() - 1;
					}
					if (probeSucc > currentSeg.getTo())
						currentSeg.setTo(probeSucc);
				}
		}
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		int maxCount = 20;
		boolean more = this.segments.size() > maxCount;
		if (!(more)) {
			maxCount = this.segments.size();
		}
		buf.append("[");
		for (int i = 0; i < maxCount; ++i) {
			buf.append(this.segments.get(i)).append(",");
		}
		if (more) {
			buf.append("...");
		} else if (maxCount > 0) {
			buf.setLength(buf.length() - 1);
		}

		buf.append("]");
		return buf.toString();
	}
}
