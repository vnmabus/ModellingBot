package es.uam.app.message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.uam.app.actions.ActionModel;
import es.uam.app.main.ValidText;
import es.uam.app.projects.ecore.Feature;

public class ReceivedMessage implements Comparable<ReceivedMessage> {

	private String msg; // mensaje completo
	private String command; // comando
	private String projectName; // comando
	private String text; // texto sobre el que se aplica el comando
	private User user;
	private Date date;
	private String id;
	private Map<String, List<ActionModel>> sentences;
	private boolean undoable = true;
	
	private static final char [] validCharacter={' ', '_', '.', ',', ';', '\'', '"', '-', '/'};

	public ReceivedMessage(String msg, User user, Date fecha, String command,String projectName, String id) {
		super();
		this.msg = msg;
		this.user = user;
		this.date = fecha;
		this.command = ValidText.validText(command, validCharacter);
		this.projectName=ValidText.validText(projectName, validCharacter);
		this.id = id;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date fecha) {
		this.date = fecha;
	}

	public String getUML() {
		/*
		 * { {# {#{+[<&person> Sara]| <&clock> 22-03-17 05:25:33} { /School add
		 * class} }|{T +add class ++add class } } }
		 */

		SimpleDateFormat d = new SimpleDateFormat("dd-MM-yy hh:mm:ss");
		String cad = "{#\n{#{+" + user.getUML() + "|\n";
		cad += "\n" + "{<&clock> " + d.format(date);
		cad += "}\n}\n";

		cad += "{ " + getMsgFormat() + "}\n}\n";
		cad += "{T\n";
		Set<String> keys = sentences.keySet();
		for (String k : keys) {
			cad += "+" + k + "\n";
			List<ActionModel> actions = sentences.get(k);
			for (ActionModel a : actions) {
				cad += "++" + a.toString() + "\n";
			}
		}
		cad += "}\n}";
		return cad;

	}

	private String getMsgFormat() {
		int maxLine = 30;
		if (msg.length() < maxLine) {
			return msg;
		}
		String[] split = msg.split(" ");
		String ret = "";
		String line = "";
		for (String s : split) {
			if (line.length() + s.length() > maxLine) {
				ret += line + "\n";
				line = s + " ";
			} else {
				line += s + " ";
			}
		}
		ret += line;
		return ret;
	}

	public String getChannel() {
		return user.getChannel();
	}

	@Override
	public String toString() {
		return "ReceivedMessage [msg=" + msg + ", command=" + command + ", sentence=" + text + ", user=" + user
				+ ", date=" + date + ", id=" + id + "]";
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public Map<String, List<ActionModel>> getSentences() {
		if (sentences == null) {
			return new HashMap<String, List<ActionModel>>();
		}
		return sentences;
	}

	public void setSentences(Map<String, List<ActionModel>> sentences) {
		this.sentences = sentences;
	}

	public void setText(String text) {
		this.text = ValidText.validText(text, validCharacter);

	}

	public boolean isUndoable() {
		return undoable;
	}

	public void setUndoable(boolean undoable) {
		this.undoable = undoable;
	}

	@Override
	public int compareTo(ReceivedMessage o) {
		return date.compareTo(o.getDate());
	}

	public boolean hasAction(String action) {
		Set<String> sentencesKeySet= sentences.keySet();
		for (String k: sentencesKeySet){
			List<ActionModel> ams= sentences.get(k);
			for (ActionModel am: ams){
				if (am.getActionName().equalsIgnoreCase(action)){
					return true;
				}
			}
		}
		return false;
		
	}

	public boolean hasElement(String name, String of) {
		Set<String> sentencesKeySet= sentences.keySet();
		for (String k: sentencesKeySet){
			List<ActionModel> ams= sentences.get(k);
			for (ActionModel am: ams){
				if (of!=null){
					if (am.getObject() instanceof Feature){
						Feature f= (Feature)am.getObject();
						if (f.getName().equalsIgnoreCase(name) && f.getParentName().equalsIgnoreCase(of)){
							return true;
						}
					}
				}else{
					if (am.getObject().getName().equalsIgnoreCase(name)){
						return true;
					}
				}
			}
		}
		return false;
	}

	public String getProjectName() {
		return projectName;
	}
	
	public List<ActionModel> getAllActions(){
		Set<String> sentence= sentences.keySet();
		List<ActionModel> actions= new ArrayList<ActionModel>();
		for (String s: sentence){
			actions.addAll(sentences.get(s));
		}
		return actions;
	}

	public boolean hasText() {
		if (text!= null && !text.equals(" ") && !text.isEmpty()){
			return true;
		}
		return false;
	}

	

}