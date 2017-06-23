
import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DSEE {
	private static HashMap<String,String> mapCN=new HashMap<String,String>();
	private static HashMap<String,String> mapEN=new HashMap<String,String>();
	private static HashMap<String,String> mapOut=new HashMap<String,String>();
	private static String urlCN="./src/data/CNenglish.xml";//已汉化好的文本文件
	private static String urlEn="./src/data/english.xml";//未汉化好的文本文件
	private static String urlOut="./src/data/out.xml";//输出缺失文本
	private static String urlCNOut="./src/data/CNout.xml";//输出已汉化好的文本
	private static Document docCN;
	private static Document docEN;
	private static Document docOut;
	private static Document docCNout;
	
	public static void main(String[] args) throws Exception{
		DocumentBuilder db=DocumentBuilderFactory.newInstance().newDocumentBuilder();//获取解析器  
		docCN=db.parse(new File(urlCN));//现有汉化文件
		docEN=db.parse(new File(urlEn));//英文标准文件
		docOut=db.newDocument();//输出文本文件
		docCNout=db.newDocument();//输出文本文件
		
		Element contentList=docOut.createElement("contentList");//父节点
		Element contentListCN=docCNout.createElement("contentList");//父节点
		
		NodeList listCN=docCN.getElementsByTagName("content");
		NodeList listEN=docEN.getElementsByTagName("content");
		
		//得到现有英文文本
		for(int i=0;i<listEN.getLength();i++){  
		    Element e = (Element) listEN.item(i);
		    mapEN.put(e.getAttribute("contentuid"), e.getTextContent());
		}
		//得到现有中文文本
		for(int i=0;i<listCN.getLength();i++){  
		    Element e = (Element) listCN.item(i);
		    mapCN.put(e.getAttribute("contentuid"),e.getTextContent());
		}
		
		//提取官方文本中，现有汉化文件缺失的文本
		int lostNum=0;
		for(int i=0;i<listEN.getLength();i++){  
			 Element e = (Element) listEN.item(i);
			 if(!mapCN.containsKey(e.getAttribute("contentuid"))){
				 lostNum++;
				 contentList.appendChild(buildElement(docOut,e.getAttribute("contentuid"),e.getTextContent()));//输出
			 }
		}
		//提取官方文本中，现有汉化文件没有汉化的文本 || 汉化完毕的文本
		int enNum=0;
		for(int i=0;i<listEN.getLength();i++){
			 Element e = (Element) listEN.item(i);
			 if(mapCN.containsKey(e.getAttribute("contentuid"))){
				 int length = mapCN.get(e.getAttribute("contentuid")).length();
				 if(length==0 || length==1){
					 length=0;
				 }else if(length==2 || length == 3){
					 length=length-1;
				 }else if(length>=4){
					 length=2;
				 }else length=0;
				 
				 if(!isChineseChar(mapCN.get(e.getAttribute("contentuid")).substring(0,length))){
					 enNum++;
					 contentList.appendChild(buildElement(docOut,e.getAttribute("contentuid"),e.getTextContent()));//输出
				 }
				 else{
					 contentListCN.appendChild(buildElement(docCNout,e.getAttribute("contentuid"),mapCN.get(e.getAttribute("contentuid"))));//输出
				 }
			 }
		}
		
		//contentList.appendChild(buildElement("",""));
		
		//最终挂载
		docCNout.appendChild(contentListCN);
		docOut.appendChild(contentList);
		outDocument(docCNout,urlCNOut);
		outDocument(docOut,urlOut);
		//输出缺失文本及漏翻条数
		System.out.println("Done！\n"+lostNum+" : "+enNum);
	}
	public static void outDocument(Document doc,String url) throws TransformerException{
		//输出这个xml文件
		File file=new File(url);
		Transformer tr=TransformerFactory.newInstance().newTransformer();
		tr.setOutputProperty("encoding","UTF-8");//编码
		tr.setOutputProperty(OutputKeys.INDENT, "yes");//输出换行
		//doc.setXmlStandalone(true);//是否有说明文档
		tr.transform(new DOMSource(doc), new StreamResult(file));
	}
	
	//判断该字符串是否在中文范围内
	public static boolean isChineseChar(String str){
		Pattern p=Pattern.compile("[\u4e00-\u9fa5]"); 
		Matcher m=p.matcher(str);
		if(m.find()){ 
			return true;
		}
		return false;
	}
	//生成子节点
	public static Element buildElement(Document doc,String id,String value){
		Element content=doc.createElement("content");
		content.setAttribute("contentuid",id);
		content.setTextContent(value);
		return content;
	}
}
