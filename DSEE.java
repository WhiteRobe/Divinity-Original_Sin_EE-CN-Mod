
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
	private static String urlCN="./src/data/CNenglish.xml";//�Ѻ����õ��ı��ļ�
	private static String urlEn="./src/data/english.xml";//δ�����õ��ı��ļ�
	private static String urlOut="./src/data/out.xml";//���ȱʧ�ı�
	private static String urlCNOut="./src/data/CNout.xml";//����Ѻ����õ��ı�
	private static Document docCN;
	private static Document docEN;
	private static Document docOut;
	private static Document docCNout;
	
	public static void main(String[] args) throws Exception{
		DocumentBuilder db=DocumentBuilderFactory.newInstance().newDocumentBuilder();//��ȡ������  
		docCN=db.parse(new File(urlCN));//���к����ļ�
		docEN=db.parse(new File(urlEn));//Ӣ�ı�׼�ļ�
		docOut=db.newDocument();//����ı��ļ�
		docCNout=db.newDocument();//����ı��ļ�
		
		Element contentList=docOut.createElement("contentList");//���ڵ�
		Element contentListCN=docCNout.createElement("contentList");//���ڵ�
		
		NodeList listCN=docCN.getElementsByTagName("content");
		NodeList listEN=docEN.getElementsByTagName("content");
		
		//�õ�����Ӣ���ı�
		for(int i=0;i<listEN.getLength();i++){  
		    Element e = (Element) listEN.item(i);
		    mapEN.put(e.getAttribute("contentuid"), e.getTextContent());
		}
		//�õ����������ı�
		for(int i=0;i<listCN.getLength();i++){  
		    Element e = (Element) listCN.item(i);
		    mapCN.put(e.getAttribute("contentuid"),e.getTextContent());
		}
		
		//��ȡ�ٷ��ı��У����к����ļ�ȱʧ���ı�
		int lostNum=0;
		for(int i=0;i<listEN.getLength();i++){  
			 Element e = (Element) listEN.item(i);
			 if(!mapCN.containsKey(e.getAttribute("contentuid"))){
				 lostNum++;
				 contentList.appendChild(buildElement(docOut,e.getAttribute("contentuid"),e.getTextContent()));//���
			 }
		}
		//��ȡ�ٷ��ı��У����к����ļ�û�к������ı� || ������ϵ��ı�
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
					 contentList.appendChild(buildElement(docOut,e.getAttribute("contentuid"),e.getTextContent()));//���
				 }
				 else{
					 contentListCN.appendChild(buildElement(docCNout,e.getAttribute("contentuid"),mapCN.get(e.getAttribute("contentuid"))));//���
				 }
			 }
		}
		
		//contentList.appendChild(buildElement("",""));
		
		//���չ���
		docCNout.appendChild(contentListCN);
		docOut.appendChild(contentList);
		outDocument(docCNout,urlCNOut);
		outDocument(docOut,urlOut);
		//���ȱʧ�ı���©������
		System.out.println("Done��\n"+lostNum+" : "+enNum);
	}
	public static void outDocument(Document doc,String url) throws TransformerException{
		//������xml�ļ�
		File file=new File(url);
		Transformer tr=TransformerFactory.newInstance().newTransformer();
		tr.setOutputProperty("encoding","UTF-8");//����
		tr.setOutputProperty(OutputKeys.INDENT, "yes");//�������
		//doc.setXmlStandalone(true);//�Ƿ���˵���ĵ�
		tr.transform(new DOMSource(doc), new StreamResult(file));
	}
	
	//�жϸ��ַ����Ƿ������ķ�Χ��
	public static boolean isChineseChar(String str){
		Pattern p=Pattern.compile("[\u4e00-\u9fa5]"); 
		Matcher m=p.matcher(str);
		if(m.find()){ 
			return true;
		}
		return false;
	}
	//�����ӽڵ�
	public static Element buildElement(Document doc,String id,String value){
		Element content=doc.createElement("content");
		content.setAttribute("contentuid",id);
		content.setTextContent(value);
		return content;
	}
}
