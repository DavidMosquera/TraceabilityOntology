package ontology.view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxEdgeLabelLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.layout.mxPartitionLayout;
import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphSelectionModel;

import ontology.controller.TraceViewController;
import ontology.model.Artifact;
import ontology.model.HasChange;
import ontology.model.NextCommand;
import ontology.model.Source;
import ontology.model.Target;
import ontology.model.Trace;

import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.border.EmptyBorder;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.JRadioButton;
import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;

public class TraceView implements TraceViewInterface{
	
	private JFrame frame;
	private TraceViewController traceViewController; 
	private mxGraph graph;
	private HashMap<String, Object> vertexHashMap;
	private HashMap<String, Object> edgeHashMap;
	private HashMap<String, List<Object>> suggestedEdges;
	
	private boolean showTracesBoolean = false;
	private boolean showNotTracedSourcesBoolean = false;
	private boolean showNotTracedTargetsBoolean = false;
	private JComboBox<Source> sourcesCombobox;
	private JComboBox<Target> targetsComboBox;
	private mxGraphComponent graphComponent;
	private mxGraph sourceGroup;
	private mxGraph targetGroup;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TraceView window = new TraceView();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TraceView() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1041, 582);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		traceViewController = new TraceViewController();
		vertexHashMap = new HashMap<String, Object>();
		edgeHashMap = new HashMap<String, Object>();
		suggestedEdges = new HashMap<String, List<Object>>();
		graph = new mxGraph();
		graph.getSelectionModel().addListener(mxEvent.CHANGE, new mxEventSource.mxIEventListener() {
			public void invoke(Object sender, mxEventObject evt) {
				if(!showTracesBoolean) {
					
				
				mxGraphSelectionModel sm = (mxGraphSelectionModel) sender;
	            final mxCell cell = (mxCell) sm.getCell();
	            graph.getModel().beginUpdate();
	            try {
	            	 for (Object cellNotSelected : graph.getChildVertices(graph.getDefaultParent())) {
			        	   if(!sm.isSelected(cellNotSelected)) {
			        		   mxCell notSelectedCell = (mxCell) cellNotSelected;
			        		   if(suggestedEdges.containsKey("suggested" + notSelectedCell.getId() + "target")) {
			        			  for(Object suggestedEdge : suggestedEdges.get("suggested" + notSelectedCell.getId() + "target")) {
			        				  graph.getModel().remove(suggestedEdge);   
			        			  }
			        			  suggestedEdges.remove("suggested" + notSelectedCell.getId() + "target");
			        			  for(String targetsKey: traceViewController.getTargets().keySet()) {
				            		((mxCell) vertexHashMap.get(targetsKey)).setVisible(true);
			        			  }
			        			  for(String sourceKey: traceViewController.getSources().keySet()) {
				            		((mxCell) vertexHashMap.get(sourceKey)).setVisible(true);
			        			  }
			        		   }
			        		   if(suggestedEdges.containsKey("suggested" + notSelectedCell.getId() + "source")) {
			        			   for(Object suggestedEdge : suggestedEdges.get("suggested" + notSelectedCell.getId() + "source")) {
			        				   graph.getModel().remove(suggestedEdge);   
			        			   }
			        			   suggestedEdges.remove("suggested" + notSelectedCell.getId() + "source");
			        			   for(String targetsKey: traceViewController.getTargets().keySet()) {
				            		((mxCell) vertexHashMap.get(targetsKey)).setVisible(true);
			        			   }
			        			   for(String sourceKey: traceViewController.getSources().keySet()) {
				            		((mxCell) vertexHashMap.get(sourceKey)).setVisible(true);
			        			   }
			        		   }
			        	   }
			           }
	            	if (cell != null && cell.isVertex() && sm.isSelected(cell)) {
		                Object edge; 
		                List<Object> edges;
		                if(traceViewController.getTargets().containsKey(cell.getId())) {
		            		edges = new ArrayList<Object>();
		            		List<Source> sources = traceViewController.whatAreThePossibleSourceArtifactsThatMayBeTraceToAnSpecificTarget(cell.getValue().toString());
		            		for(String sourceKey: traceViewController.getSources().keySet()) {
		            			((mxCell) vertexHashMap.get(sourceKey)).setVisible(false);
		                	}
		            		for(String targetKey: traceViewController.getTargets().keySet()) {
		            			((mxCell) vertexHashMap.get(targetKey)).setVisible(false);
		            		}
		            		cell.setVisible(true);
		            		for(Source source: sources) {
		            			((mxCell) vertexHashMap.get(source.getHashKey())).setVisible(true);
		            			edge = graph.insertEdge(graph.getDefaultParent(), "suggested" + cell.getId() + " " + source.getHashKey() , "SUGGESTED SOURCE", vertexHashMap.get(cell.getId()) , vertexHashMap.get(source.getHashKey()));
		                		edges.add(edge);
		                		graph.getModel().setStyle(edge, getSuggestedTracedStyle());	
		            		}
		                	suggestedEdges.put("suggested" + cell.getId() + "source", edges);
		                }
		                if(traceViewController.getSources().containsKey(cell.getId())) {
		                	edges = new ArrayList<Object>();
		                	List<Target> targets = traceViewController.whatAreThePossibleTargetArtifactsThatMayBeTraceToAnSpecificSource(cell.getValue().toString());
		                	for(String targetsKey: traceViewController.getTargets().keySet()) {
		            			((mxCell) vertexHashMap.get(targetsKey)).setVisible(false);
		                	}
		                	for(String sourceKey: traceViewController.getSources().keySet()) {
		            			((mxCell) vertexHashMap.get(sourceKey)).setVisible(false);
		                	}
		                	cell.setVisible(true);
		                	for(Target target: targets) {
		                		((mxCell) vertexHashMap.get(target.getHashKey())).setVisible(true);
		                		edge = graph.insertEdge(graph.getDefaultParent(), "suggested" + cell.getId() + " " + target.getHashKey() , "SUGGESTED TARGET", vertexHashMap.get(cell.getId()), vertexHashMap.get(target.getHashKey()));
		                		edges.add(edge);
		                		graph.getModel().setStyle(edge, getSuggestedTracedStyle());
		        				
		                	}
		                	suggestedEdges.put("suggested" + cell.getId() + "target", edges);
		                }   
		                
		            }
		           resetLayout();
            	}finally {
            		mxMorphing morph = new mxMorphing(graphComponent); 
     	           	morph.addListener(mxEvent.DONE, new mxIEventListener() {
     				public void invoke(Object sender, mxEventObject evt) {
     					graph.getModel().endUpdate();
     					graphComponent.scrollCellToVisible(cell, true);
     				}  
     	           });
     	           morph.startAnimation(); 
            	}
				}
			}
		});
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		graphComponent = new mxGraphComponent(graph);
		graphComponent.getViewport().setBackground(Color.WHITE);
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(graphComponent, BorderLayout.CENTER);
		
		JPanel actionsPanel = new JPanel();
		panel.add(actionsPanel, BorderLayout.NORTH);
		
		JRadioButton showTraces = new JRadioButton("Show traces");
		showTraces.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(showTracesBoolean) {
					showTracesBoolean = false;
					stopShowingCurrentTraces();
				}else {
					showTracesBoolean = true;
					showCurrentTraces();
				}
				graph.getModel().beginUpdate();
				resetLayout();
				mxMorphing morph = new mxMorphing(graphComponent); 
 	           	morph.addListener(mxEvent.DONE, new mxIEventListener() {
 				public void invoke(Object sender, mxEventObject evt) {
 					graph.getModel().endUpdate();
 				}  
 	           });
 	           morph.startAnimation();
			}
		});
		actionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		showTraces.setForeground(new Color(0, 0, 0));
		showTraces.setFont(new Font("Tahoma", Font.PLAIN, 16));
		showTraces.setBackground(new Color(240, 240, 240));
		showTraces.setActionCommand("Show traces");
		actionsPanel.add(showTraces);
		
		JRadioButton showNotTracedSources = new JRadioButton("Show not traced sources");
		showNotTracedSources.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(showNotTracedSourcesBoolean) {
					showNotTracedSourcesBoolean = false;
					stopShowingNotTracedSources();
				}else {
					showNotTracedSourcesBoolean = true;
					showNotTracedSources();
				}
			}
		});
		showNotTracedSources.setFont(new Font("Tahoma", Font.PLAIN, 16));
		actionsPanel.add(showNotTracedSources);
		
		JRadioButton showNotTracedTargets = new JRadioButton("Show not traced targets");
		showNotTracedTargets.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(showNotTracedTargetsBoolean) {
					showNotTracedTargetsBoolean = false;
					stopShowingNotTracedTargets();
				}else {
					showNotTracedTargetsBoolean = true;
					showNotTracedTargets();
				}
			}
		});
		showNotTracedTargets.setFont(new Font("Tahoma", Font.PLAIN, 16));
		showNotTracedTargets.setActionCommand("Show not traced targets");
		actionsPanel.add(showNotTracedTargets);
		
		JPanel addTracePanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) addTracePanel.getLayout();
		frame.getContentPane().add(addTracePanel, BorderLayout.SOUTH);
		
		JLabel sourceLabel = new JLabel("Source:");
		sourceLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		addTracePanel.add(sourceLabel);
		List<Source> orederedSourceKey = new ArrayList<Source>();
		for(String sourceKey : this.traceViewController.getSources().keySet()) {
			orederedSourceKey.add(traceViewController.getSources().get(sourceKey));
		}
		Collections.sort(orederedSourceKey, new Comparator<Source>() {
			public int compare(Source o1, Source o2) {
				return extractInt(o1.toString()) - extractInt(o2.toString());
		    }
		    int extractInt(String s) {
		        String num = s.replaceAll("\\D", "");
		        return num.isEmpty() ? 0 : Integer.parseInt(num);
		    }
		});
		sourcesCombobox = new JComboBox<Source>();
		sourcesCombobox.setBackground(new Color(255, 255, 255));
		sourcesCombobox.setForeground(new Color(0, 0, 0));
		sourcesCombobox.setFont(new Font("Tahoma", Font.PLAIN, 16));
		sourcesCombobox.setMaximumSize(sourcesCombobox.getPreferredSize());
		for(Source orderedSource: orederedSourceKey) {
			sourcesCombobox.addItem(orderedSource);
		}
		
		sourcesCombobox.setPrototypeDisplayValue(new Source("-#---------------- Sources ----------------"));
		addTracePanel.add(sourcesCombobox);
		
		JLabel targetLabel = new JLabel("Target:");
		targetLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		addTracePanel.add(targetLabel);
		List<Target> orederedTargetKey = new ArrayList<Target>();
		for(String targetKey : this.traceViewController.getTargets().keySet()) {
			orederedTargetKey.add(traceViewController.getTargets().get(targetKey));
		}
		Collections.sort(orederedTargetKey, new Comparator<Target>() {
			public int compare(Target o1, Target o2) {
				return extractInt(o1.toString()) - extractInt(o2.toString());
		    }
		    int extractInt(String s) {
		        String num = s.replaceAll("\\D", "");
		        // return 0 if no digits found
		        return num.isEmpty() ? 0 : Integer.parseInt(num);
		    }
		});
		targetsComboBox = new JComboBox<Target>();
		targetsComboBox.setForeground(new Color(0, 0, 0));
		targetsComboBox.setBackground(new Color(255, 255, 255));
		targetsComboBox.setFont(new Font("Tahoma", Font.PLAIN, 16));
		for(Target orderedTarget : orederedTargetKey) {
			targetsComboBox.addItem(orderedTarget);
		}
		targetsComboBox.setPrototypeDisplayValue(new Target("-#---------------- Targets ----------------"));
		targetsComboBox.setMaximumSize(targetsComboBox.getPreferredSize());
		addTracePanel.add(targetsComboBox);
		
		JButton traceButton = new JButton("Trace!");
		traceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addTrace();
			}
		});
		traceButton.setBackground(new Color(240, 240, 240));
		traceButton.setForeground(new Color(0, 0, 0));
		traceButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		addTracePanel.add(traceButton);
		
		drawCommands();
		drawDetectedChanges();
		
		mxFastOrganicLayout a = new mxFastOrganicLayout(graph);
		a.setForceConstant(250);
		a.execute(graph.getDefaultParent());
		
		drawSources();
		drawTargets();
	}

	public void showNotTracedSources() {
		this.traceViewController.isThereAnySourceArtifactThatDoesNotHaveALinkToATargetArtifact();
		graph.getModel().beginUpdate();
		try {
			Source source;
			for(String sourceKey : this.traceViewController.getSources().keySet()) {
				source = this.traceViewController.getSources().get(sourceKey);
				if(source.getStatus() == Artifact.TraceStatus.NOT_TRACED) {
					graph.getModel().setStyle(vertexHashMap.get(sourceKey), getSourceWithoutTrace()); 
				}
			}
		}finally {
			graph.getModel().endUpdate();
		}
	}

	public void stopShowingNotTracedSources() {
		graph.getModel().beginUpdate();
		try {
			for(String sourceKey : this.traceViewController.getSources().keySet()) {
				graph.getModel().setStyle(vertexHashMap.get(sourceKey), getSourceStyle()); 
			}
		}finally {
			graph.getModel().endUpdate();
		}
	}

	public void showNotTracedTargets() {
		this.traceViewController.isThereAnyTargetArtifactThatDoesNotHaveALinkToASourceArtifact();
		graph.getModel().beginUpdate();
		try {
			Target target;
			for(String targetKey : this.traceViewController.getTargets().keySet()) {
				target = this.traceViewController.getTargets().get(targetKey);
				if(target.getStatus() == Artifact.TraceStatus.NOT_TRACED) {
					graph.getModel().setStyle(vertexHashMap.get(targetKey), getTargetWithoutTrace()); 
				}
			}
		}finally {
			graph.getModel().endUpdate();
		}
	}

	public void stopShowingNotTracedTargets() {
		graph.getModel().beginUpdate();
		try {
			for(String targetKey : this.traceViewController.getTargets().keySet()) {
				graph.getModel().setStyle(vertexHashMap.get(targetKey), getTargetStyle()); 
			}
		}finally {
			graph.getModel().endUpdate();
		}
	}

	public void showCurrentTraces() {
		this.traceViewController.whatAreTheTracesBetweenMyArtifacts();
		graph.getModel().beginUpdate();
		try {
			Trace trace;
			Object edge;
			for(String traceKey : this.traceViewController.getTraces().keySet()) {
				trace = this.traceViewController.getTraces().get(traceKey);
				edge = graph.insertEdge(graph.getDefaultParent(), trace.getHashKey(), "TRACE", this.vertexHashMap.get(trace.getHasSource().getHashKey()), this.vertexHashMap.get(trace.getHasTarget().getHashKey()));
				this.graph.getModel().setStyle(edge, getTraceStyle());
				this.edgeHashMap.put(traceKey, edge);
			}
		}finally {
			graph.getModel().endUpdate();
		}
	}

	public void stopShowingCurrentTraces() {
		graph.getModel().beginUpdate();
		try {
			for(String traceKey : this.traceViewController.getTraces().keySet()) {
				graph.getModel().remove(this.edgeHashMap.get(traceKey));
				this.edgeHashMap.remove(traceKey);
			}
		}finally {
			graph.getModel().endUpdate();
		}
	}

	public void addTrace() {
		this.traceViewController.addTrace((Source) sourcesCombobox.getSelectedItem(), (Target) targetsComboBox.getSelectedItem());
	}

	public void showSuggestedTargets() {
		// IMPLEMENTED BUT IN LISTENER
		
	}

	public void stopShowingSuggestedTargets() {
		// IMPLEMENTED BUT IN LISTENER
		
	}

	public void showSuggestedSources() {
		// IMPLEMENTED BUT IN LISTENER
		
	}

	public void stopShowingSuggestedSources() {
		// IMPLEMENTED BUT IN LISTENER
		
	}

	public void deleteTrace() {
		// TODO Auto-generated method stub
		
	}

	public void drawTargets() {
		graph.getModel().beginUpdate();
		try {
			for (String traceKey : this.traceViewController.getTargets().keySet()) {
				graph.getModel().setStyle(this.vertexHashMap.get(traceKey), getTargetStyle());
			}
		}finally {
			graph.getModel().endUpdate();
		}
	}

	public void drawSources() {
		Object parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		int i = 1;
		try {
			Object targetVertix; 
			Artifact source;
			for (String sourcesKey: this.traceViewController.getSources().keySet()) {
				source = this.traceViewController.getSources().get(sourcesKey);
				targetVertix = graph.insertVertex(parent, source.getHashKey(), source.toString(), 10, i*40, source.toString().length()*8, 30);
				this.vertexHashMap.put(source.getHashKey(), targetVertix);
				//graph.getModel().setStyle( this.vertexHashMap.get(targetKey), );
				graph.getModel().setStyle( this.vertexHashMap.get(sourcesKey), getSourceStyle());
				i++;
			}
        } finally {
            graph.getModel().endUpdate();
        }
	}

	public void resetLayout() {
		mxFastOrganicLayout a = new mxFastOrganicLayout(this.graph);
		a.setForceConstant(250);
		a.execute(graph.getDefaultParent());
	}

	public void drawCommands() {
		Object parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		int i = 1;
		try {
			Object targetVertix; 
			Object targetEdge; 
			Artifact target; 
			NextCommand nextCommand; 
			for (String targetKey: this.traceViewController.getCommands().keySet()) {
				target = this.traceViewController.getCommands().get(targetKey);
				targetVertix = graph.insertVertex(parent, target.getHashKey(), target.toString(), 875, 40*i, target.toString().length()*11, 30);
				this.vertexHashMap.put(target.getHashKey(), targetVertix);
				//graph.getModel().setStyle( this.vertexHashMap.get(targetKey), );
				graph.getModel().setStyle( this.vertexHashMap.get(targetKey), getNeutralStyle());
				i++;
			}
			for (String nextCommandKey : this.traceViewController.getNextCommands().keySet()) {
				nextCommand = this.traceViewController.getNextCommands().get(nextCommandKey);
				targetEdge = graph.insertEdge(parent, nextCommandKey, "Next", this.vertexHashMap.get(nextCommand.getSourceCommand().getHashKey()), this.vertexHashMap.get(nextCommand.getTargetCommand().getHashKey()));
				this.edgeHashMap.put(nextCommandKey, targetEdge);
			}
        } finally {
            graph.getModel().endUpdate();
        }
	}

	public void drawDetectedChanges() {
		Object parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		int i = 1;
		try {
			Object targetVertix; 
			Object targetEdge; 
			Artifact target; 
			HasChange nextCommand; 
			for (String targetKey: this.traceViewController.getChanges().keySet()) {
				target = this.traceViewController.getChanges().get(targetKey);
				targetVertix = graph.insertVertex(parent, target.getHashKey(), target.toString(), 370, 40*i, 8*target.toString().length(), 30);
				this.vertexHashMap.put(target.getHashKey(), targetVertix);
				graph.getModel().setStyle( this.vertexHashMap.get(targetKey), getTargetStyle());
				i++;
			}
			for (String nextCommandKey : this.traceViewController.getHasChange().keySet()) {
				nextCommand = this.traceViewController.getHasChange().get(nextCommandKey);
				targetEdge = graph.insertEdge(parent, nextCommandKey, "hasChange", this.vertexHashMap.get(nextCommand.getSourceCommand().getHashKey()), this.vertexHashMap.get(nextCommand.getChange().getHashKey()));
				this.edgeHashMap.put(nextCommandKey, targetEdge);
			}
        } finally {
        	graph.getModel().endUpdate();
        }
	}
	
	public String getTargetStyle() {
		String style = ""
				+ mxConstants.STYLE_FONTCOLOR + "=#FFFFFF;"
				+ mxConstants.STYLE_FILLCOLOR + "=#000000;"
				+ mxConstants.STYLE_FONTSIZE+"=16";
		
		return style;
	}
	
	public String getSourceStyle() {
		String style = ""
				+ mxConstants.STYLE_FONTCOLOR + "=#000000;"
				+ mxConstants.STYLE_FILLCOLOR + "=#FFFFFF;"
				+ mxConstants.STYLE_FONTSIZE+"=16";
		return style;
	}
	
	public String getNeutralStyle() {
		String style = ""
				+ mxConstants.STYLE_FONTCOLOR + "=#000000;"
				+ mxConstants.STYLE_FILLCOLOR + "=#DADADA;"
				+ mxConstants.STYLE_FONTSIZE+"=16";
		return style;
	}
	
	public String getSourceWithoutTrace(){
		String style = ""
				+ mxConstants.STYLE_FONTCOLOR + "=#000000;"
				+ mxConstants.STYLE_FILLCOLOR + "=#FFFFFF;"
				+ mxConstants.STYLE_LABEL_BORDERCOLOR + "=#FF0000;"
				+ mxConstants.STYLE_FONTSIZE+"=16";
		return style;
	}
	public String getTargetWithoutTrace(){
		String style = ""
				+ mxConstants.STYLE_FONTCOLOR + "=#FFFFFF;"
				+ mxConstants.STYLE_FILLCOLOR + "=#000000;"
				+ mxConstants.STYLE_LABEL_BORDERCOLOR + "=#FF0000;"
				+ mxConstants.STYLE_FONTSIZE+"=16";
		return style;
	}
	public String getTraceStyle() {
		String style = ""
				+ mxConstants.STYLE_STROKECOLOR + "=#00B710;"
				+ mxConstants.STYLE_FONTSIZE+"=16";
		return style;
	}
	public String getSuggestedTracedStyle() {
		String style = ""
				+ mxConstants.STYLE_STROKECOLOR + "=#FFB100;"
				+ mxConstants.STYLE_FONTSIZE+"=16";
		return style;
	}
}
