package com.entitylinking.linking.bean;

/**
 * 文本的数据结构
 * @author HP
 *
 */
public class Text {
	/**文档名称*/
	private String textName;
	/**文档内容*/
	private String content;
	/**文档对应的实体图*/
	private EntityGraph entityGraph;
	/**文档中上下文*/
	private TextContext textContext;
	
	public Text(){
		entityGraph = new EntityGraph();
		textContext = new TextContext();
	}
	
	public Text(String content){
		this.content = content;
		this.entityGraph = new EntityGraph();
		this.textContext = new TextContext();
	}
	
	public String getTextName() {
		return textName;
	}
	public void setTextName(String textName) {
		this.textName = textName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public EntityGraph getEntityGraph() {
		return entityGraph;
	}
	public void setEntityGraph(EntityGraph entityGraph) {
		this.entityGraph = entityGraph;
	}
	public TextContext getTextContext() {
		return textContext;
	}
	public void setTextContext(TextContext textContext) {
		this.textContext = textContext;
	}
	
	
}
