package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.repeat;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.avcompris.util.XMLUtils;
import com.google.common.collect.Iterables;

import fr.univmobile.backend.core.Poi;
import fr.univmobile.backend.core.PoiDataSource;

/**
 * Class to hold calculation and manipulation of the hierarchical structure that
 * holds POIs together.
 */
public class PoiTreeUtils {

	public PoiTreeUtils(final PoiDataSource pois) throws IOException {

		this.pois = checkNotNull(pois, "pois");

		reload();
	}

	private final Set<PoiNode> roots = new HashSet<PoiNode>();

	private final Map<Integer, PoiNode> nodes = new HashMap<Integer, PoiNode>();

	public synchronized void reload() throws IOException {

		nodes.clear();
		roots.clear();

		handlePoiCount = 0;

		for (final Poi poi : pois.getAllByInt("uid").values()) {

			if (!(poi.getActive() == "true")) {
				continue;
			}

			if (poi.isDeleted()) {
				continue;
			}

			// final int uid = entry.getKey();

			// System.out.println("---" + uid);

			handlePoi(poi);
		}

		System.out.println("Analyzing " + handlePoiCount + " (Done.)");
	}

	private int handlePoiCount = 0;

	public synchronized void dump(final PrintWriter pw) throws IOException {

		System.out.println("Dumping roots: " + roots.size());

		pw.println("<poiTree>");

		dumpCount = 0;

		for (final PoiNode root : roots) {

			dump(pw, root, 0);
		}

		pw.println("</poiTree>");

		pw.flush();

		System.out.println("Dumping " + dumpCount + " (Done.)");
	}

	private int dumpCount = 0;

	private void dump(final PrintWriter pw, final PoiNode node, final int indent)
			throws IOException {

		++dumpCount;

		if (dumpCount % 1000 == 0) {

			System.out.println("Dumping " + dumpCount + "...");
		}

		final String prefix = repeat(' ', 4 * indent);

		final StringBuilder startTag = new StringBuilder("<poi uid=\"").append(
				node.uid).append("\"");
		startTag.append(" name=\"")
				.append(XMLUtils.xmlEncode(node.poi.getName())).append("\"");

		if (!node.poi.isNullUniversities()) {
			startTag.append(" universityId=\"")
					.append(node.poi.getUniversityIds()[0]).append("\"");
		}

		startTag.append(" typeId=\"").append(node.poi.getPoiTypeIds()[0])
				.append("\"");
		startTag.append(" typeLabel=\"")
				.append(XMLUtils.xmlEncode(node.poi.getPoiTypeLabels()[0]))
				.append("\"");

		if (!node.poi.isNullPoiCategoryIds()) {
			startTag.append(" categoryId=\"")
					.append(node.poi.getPoiCategoryIds()[0]).append("\"");
			startTag.append(" categoryLabel=\"")
					.append(XMLUtils.xmlEncode(node.poi.getPoiCategoryLabels()[0]))
					.append("\"");
		}

		if (node.children.isEmpty()) {

			pw.println(prefix + startTag + "/>");

		} else {

			pw.println(prefix + startTag + ">");

			for (final PoiNode child : node.children) {

				dump(pw, child, indent + 1);
			}

			pw.println(prefix + "</poi>");
		}
	}

	private PoiNode handlePoi(final Poi poi) {

		checkNotNull(poi, "poi");

		final int uid = poi.getUid();

		final PoiNode cached = nodes.get(uid);

		if (cached != null) {
			return cached;
		}

		final PoiNode node = new PoiNode(poi);

		// System.out.println("   Storing: " + uid);

		++handlePoiCount;

		if (handlePoiCount % 1000 == 0) {
			System.out.println("Analyzing " + handlePoiCount + "...");
		}

		nodes.put(uid, node);

		if (poi.isNullParentUid()) {

			if (poi.getUniversityIds().length != 0) {

				// System.out.println("  adding root: " + uid);

				roots.add(node);
			}

		} else {

			final int parentUid = poi.getParentUid();

			// System.out.println("  " + uid + ".parent: " + parentUid);

			final Poi parentPoi = pois.getByUid(parentUid);

			final PoiNode parentNode = handlePoi(parentPoi);

			for (final int childUid : parentPoi.getChildren()) {

				// System.out.println("  " + parentUid + ".child: " + childUid);

				if (childUid == uid) {
					break;
				}

				final PoiNode childNode = handlePoi(pois.getByUid(childUid));

				if (!parentNode.children.contains(childNode)) {
					parentNode.addChild(childNode);
				}
			}

			parentNode.addChild(node);
		}

		return node;
	}

	private final PoiDataSource pois;

	public Poi[] getRoots() {

		// System.out.println("nodes: " + nodes.size());
		// System.out.println("roots: " + roots.size());

		// for (final PoiNode root : roots) {

		// System.out.println("root: " + root.uid);
		// }

		final PoiNode[] nodes = Iterables.toArray(roots, PoiNode.class);

		final Poi[] pois = new Poi[nodes.length];

		for (int i = 0; i < nodes.length; ++i) {

			pois[i] = nodes[i].poi;
		}

		return pois;
	}

	private static final class PoiNode {

		@Override
		public int hashCode() {

			return uid;
		}

		@Override
		public boolean equals(final Object o) {

			if (o == null || !PoiNode.class.equals(o.getClass())) {
				return false;
			}

			final PoiNode node = (PoiNode) o;

			if (poi == node.poi) { // Return faster
				return true;
			}

			return uid == node.uid;
		}

		private final Poi poi;
		public final int uid;

		public PoiNode(final Poi poi) {

			this.poi = checkNotNull(poi, "poi");

			uid = poi.getUid();
		}

		public void addChild(final PoiNode child) {

			checkNotNull(child, "child");

			if (children.contains(child)) {
				throw new IllegalStateException("Child: " + child.uid
						+ " has already been added to: " + uid);
			}

			children.add(child);
		}

		private final List<PoiNode> children = new ArrayList<PoiNode>();
	}
}
