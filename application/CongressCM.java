import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.GroupAction;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.filter.VisibilityFilter;
import prefuse.action.layout.AxisLabelLayout;
import prefuse.action.layout.AxisLayout;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.controls.ToolTipControl;
import prefuse.data.Table;
import prefuse.data.expression.AndPredicate;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.io.DelimitedTextTableReader;
import prefuse.data.query.RangeQueryBinding;
import prefuse.data.query.SearchQueryBinding;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.AxisRenderer;
import prefuse.render.Renderer;
import prefuse.render.RendererFactory;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.UpdateListener;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;
import prefuse.visual.expression.VisiblePredicate;
import prefuse.visual.sort.ItemSorter;

/**
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class CongressCM extends JPanel {

	public static void main(String[] args) {
		UILib.setPlatformLookAndFeel();

		JFrame f = demo();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

	public static JFrame demo() {

		Table t = null;
		try {
			//change the path here.
			t = new DelimitedTextTableReader().readTable("WHOCountryMedals.txt"); 
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(1);
		}

		// change the title here.
		JFrame frame = new JFrame("W H O |  C O U N T R Y  M E D A L S");
		frame.setContentPane(new CongressCM(t));
		frame.pack();

		return frame;
	}

	public static JPanel demoOnly() {

		Table t = null;
		try {
			//change the path here.
			t = new DelimitedTextTableReader().readTable("WHOCountryMedals.txt"); 
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(1);
		}
		// change the title here. 
		JPanel panel = new JPanel();
		panel.add(new CongressCM(t));

		return panel;
	}

	// ------------------------------------------------------------------------

	//this is the main field for the GDP Value
	//
	private static final String TOTAL_RECEIPTS = "TotalMedals";
	private static final String RECEIPTS = "TotalMedals";
	//

	//private String m_title = "Congress";
	private String m_totalStr;
	private double m_totalMoney = 1000000000;
	private int m_totalPeople = 10000;
	private JFastLabel m_total = new JFastLabel(m_totalPeople+" Country: "+m_totalMoney);
	private JFastLabel m_details;

	private Visualization m_vis;
	private Display m_display;
	private Rectangle2D m_dataB = new Rectangle2D.Double();
	private Rectangle2D m_xlabB = new Rectangle2D.Double();
	private Rectangle2D m_ylabB = new Rectangle2D.Double();

	public CongressCM(Table t) {
		super(new BorderLayout());

		final Visualization vis = new Visualization();
		m_vis = vis;

		final String group = "Country";


		//the minimum amount of GDP to be displayed.
		Predicate p = (Predicate)
				ExpressionParser.parse("["+TOTAL_RECEIPTS+"] >= 10"); 
		VisualTable vt = vis.addTable(group, t, p);

		//the display on the top right hand corner of the display. Put the name of the column.
		//
		vt.addColumn("label", 
				"CONCAT(CAP(Country), ' (', " 
						+ "CAP([NOC]), '-', [Country], "
						+ "') ', '- T: ', [TotalMedals], ', B: ', [Bronze], ', S: ', [Silver], ', G: ', [Gold])");
		//

		vis.setRendererFactory(new RendererFactory() {
			AbstractShapeRenderer sr = new ShapeRenderer();
			Renderer arY = new AxisRenderer(Constants.RIGHT, Constants.TOP);
			Renderer arX = new AxisRenderer(Constants.CENTER, Constants.FAR_BOTTOM);

			public Renderer getRenderer(VisualItem item) {
				return item.isInGroup("ylab") ? arY :
					item.isInGroup("xlab") ? arX : sr;
			}
		});

		// this will check for all the value. just change the last value. 
		// this is the range value.
		RangeQueryBinding  receiptsQ = new RangeQueryBinding(vt, RECEIPTS);
		// this is at the bottom of the value. if there is more than one type of graph, use this.
		// i commented because there is no use for this graph.
		//ListQueryBinding   yearsQ    = new ListQueryBinding(vt, "Country");
		// The value to use to search. 
		SearchQueryBinding searchQ   = new SearchQueryBinding(vt, "Country");


		AndPredicate filter = new AndPredicate(searchQ.getPredicate());
		//remove this if you activate the yearsQ
		//filter.add(yearsQ.getPredicate());
		filter.add(receiptsQ.getPredicate());

		// set up the x axis and y axis.
		AxisLayout xaxis = new AxisLayout(group, "NOC",
				Constants.X_AXIS, VisiblePredicate.TRUE); 
		AxisLayout yaxis = new AxisLayout(group, RECEIPTS,
				Constants.Y_AXIS, VisiblePredicate.TRUE);
		//yaxis.setScale(Constants.LOG_SCALE);
		yaxis.setRangeModel(receiptsQ.getModel());
		receiptsQ.getNumberModel().setValueRange(0,100,0,100);

		xaxis.setLayoutBounds(m_dataB);
		yaxis.setLayoutBounds(m_dataB);

		//Might need to change label here
		AxisLabelLayout ylabels = new AxisLabelLayout("ylab", yaxis, m_ylabB);
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(0);
		ylabels.setNumberFormat(nf);
		//

		AxisLabelLayout xlabels = new AxisLabelLayout("xlab", xaxis, m_xlabB, 30);
		vis.putAction("xlabels", xlabels);

		// dems = blue, reps = red, other = gray
		int[] palette = new int[] {
				ColorLib.rgb(150,150,255), ColorLib.rgb(255,150,150),
				ColorLib.rgb(180,180,180)
		};

		// set the value of this "GDP"
		//
		DataColorAction color = new DataColorAction(group, "TotalMedals",
				Constants.ORDINAL, VisualItem.STROKECOLOR, palette);
		//

		//int[] shapes = new int[] { Constants.SHAPE_RECTANGLE, Constants.SHAPE_DIAMOND };
		// DataShapeAction shape = new DataShapeAction(group, "Senate", shapes);

		Counter cntr = new Counter(group);

		ActionList draw = new ActionList();
		draw.add(cntr);
		//
		draw.add(color);
		//
		//draw.add(shape);
		draw.add(xaxis);
		draw.add(yaxis);
		draw.add(ylabels);
		draw.add(new ColorAction(group, VisualItem.FILLCOLOR, 0));
		draw.add(new RepaintAction());
		vis.putAction("draw", draw);

		ActionList update = new ActionList();
		update.add(new VisibilityFilter(group, filter));
		update.add(cntr);
		update.add(xaxis);
		update.add(yaxis);
		update.add(ylabels);
		update.add(new RepaintAction());
		vis.putAction("update", update);

		UpdateListener lstnr = new UpdateListener() {
			public void update(Object src) {
				vis.run("update");
			}
		};
		filter.addExpressionListener(lstnr);

		// --------------------------------------------------------------------
		// STEP 4: set up a display and ui components to show the visualization

		m_display = new Display(vis);
		m_display.setItemSorter(new ItemSorter() {
			public int score(VisualItem item) {
				int score = super.score(item);
				if ( item.isInGroup(group) )
					score += item.getInt(TOTAL_RECEIPTS);
				return score;
			}
		});
		m_display.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		m_display.setSize(715,450);
		m_display.setHighQuality(true);
		m_display.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				displayLayout();
			}
		});
		displayLayout();

		m_details = new JFastLabel();
		m_details.setPreferredSize(new Dimension(75,20));
		m_details.setVerticalAlignment(SwingConstants.BOTTOM);

		m_total.setPreferredSize(new Dimension(500,20));
		m_total.setHorizontalAlignment(SwingConstants.RIGHT);
		m_total.setVerticalAlignment(SwingConstants.BOTTOM);

		ToolTipControl ttc = new ToolTipControl("label");
		Control hoverc = new ControlAdapter() {
			public void itemEntered(VisualItem item, MouseEvent evt) {
				if ( item.isInGroup(group) ) {
					m_total.setText(item.getString("label"));
					item.setFillColor(item.getStrokeColor());
					item.setStrokeColor(ColorLib.rgb(0,0,0));
					item.getVisualization().repaint();
				}
			}
			public void itemExited(VisualItem item, MouseEvent evt) {
				if ( item.isInGroup(group) ) {
					m_total.setText(m_totalStr);
					item.setFillColor(item.getEndFillColor());
					item.setStrokeColor(item.getEndStrokeColor());
					item.getVisualization().repaint();
				}
			}
		};
		m_display.addControlListener(ttc);
		m_display.addControlListener(hoverc);


		// --------------------------------------------------------------------        
		// STEP 5: launching the visualization

		this.addComponentListener(lstnr);

		// details
		Box infoBox = new Box(BoxLayout.X_AXIS);
		infoBox.add(Box.createHorizontalStrut(5));
		infoBox.add(m_details);
		infoBox.add(Box.createHorizontalGlue());
		infoBox.add(Box.createHorizontalStrut(5));
		infoBox.add(m_total);
		infoBox.add(Box.createHorizontalStrut(5));

		// set up search box
		JSearchPanel searcher = searchQ.createSearchPanel();
		searcher.setLabelText("Search: ");
		searcher.setBorder(BorderFactory.createEmptyBorder(5,5,5,0));

		// create dynamic queries
		Box radioBox = new Box(BoxLayout.X_AXIS);
		radioBox.add(Box.createHorizontalStrut(5));
		radioBox.add(searcher);
		radioBox.add(Box.createHorizontalGlue());
		radioBox.add(Box.createHorizontalStrut(5));
		//radioBox.add(yearsQ.createRadioGroup());
		radioBox.add(Box.createHorizontalStrut(16));

		/*JRangeSlider slider = receiptsQ.createVerticalRangeSlider();
		slider.setThumbColor(null);
		slider.setMinExtent(150000);
		slider.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				m_display.setHighQuality(false);
			}
			public void mouseReleased(MouseEvent e) {
				m_display.setHighQuality(true);
				m_display.repaint();
			}
		});*/

		vis.run("draw");
		vis.run("xlabels");

		add(infoBox, BorderLayout.NORTH);
		add(m_display, BorderLayout.CENTER);
		//add(slider, BorderLayout.EAST);
		add(radioBox, BorderLayout.SOUTH);
		UILib.setColor(this, ColorLib.getColor(255,255,255), Color.GRAY);
		//slider.setForeground(Color.LIGHT_GRAY);
		UILib.setFont(radioBox, FontLib.getFont("Tahoma", 15));
		m_details.setFont(FontLib.getFont("Tahoma", 18));
		m_total.setFont(FontLib.getFont("Tahoma", 16));
	}

	public void displayLayout() {
		Insets i = m_display.getInsets();
		int w = m_display.getWidth();
		int h = m_display.getHeight();
		int iw = i.left+i.right;
		int ih = i.top+i.bottom;
		int aw = 85;
		int ah = 15;

		m_dataB.setRect(i.left, i.top, w-iw-aw, h-ih-ah);
		m_xlabB.setRect(i.left, h-ah-i.bottom, w-iw-aw, ah-10);
		m_ylabB.setRect(i.left, i.top, w-iw, h-ih-ah);

		m_vis.run("update");
		m_vis.run("xlabels");
	}

	private class Counter extends GroupAction {
		public Counter(String group) {
			super(group);
		}
		public void run(double frac) {
			double total = 0;
			int count = 0;
			VisualItem item = null;
			Iterator items = m_vis.visibleItems(m_group);
			while ( items.hasNext() ) {
				item = (VisualItem)items.next();
				//total += item.getDouble("Total Receipts");
				++count;
			}
			m_totalMoney = total;
			m_totalPeople = count;

			if ( count == 1 ) {
				m_totalStr = item.getString("label");
			} else {
				m_totalStr = count + " Candidates receiving " +
						NumberFormat.getCurrencyInstance().format(total);
			}

			m_total.setText(m_totalStr);
		}
	}
}