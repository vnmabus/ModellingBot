package es.uam.app.channels.telegramCommands;

import org.telegram.telegrambots.api.objects.Update;

import es.uam.app.channels.CommandList;
import es.uam.app.main.exceptions.ProjectNotFoundException;
import es.uam.app.message.SendMessageExc;
import projectHistory.Msg;

public class Show extends TelegramCommand {

	private static final String SHOW_MSG = "Choose the project to see.";

	public Show(TelegramControl tChannel) {
		super(tChannel);

	}

	@Override
	public String getCommand() {
		return "show";
	}

	@Override
	public String getDescription() {
		return "show a project";
	}

	@Override
	public void commandAction(Update update) {
		this.removerUserTalk(update.getMessage().getChatId(), update.getMessage().getFrom());
		String text = update.getMessage().getText();
		String[] split = text.split(" ");
		// Si el comando no tiene argumentos
		if (split.length == 1) {
			String project = this.getProject(update.getMessage().getChatId());
			// Comprobamos si el chat tiene un proyecto asignado
			if (project == null || project.equals("")) {
				// si el chat no tiene un proyecto asignado,
				// enviamos la imagen con todos los proyectos y
				// esperamos respuesta.
				this.setState(update.getMessage().getChatId());
				tChannel.write(update, CommandList.PROJECT_USER_ACCESS,null, null, null);

			} else {
				this.setStandardState(update.getMessage().getChatId());
				tChannel.write(update,CommandList.BASE_CASE, project, null, null);
			}

		} else {
			this.setStandardState(update.getMessage().getChatId());
			String text2 = text.replace(split[0] + " ", "");
			tChannel.write(update,CommandList.BASE_CASE, text2, null, null);
		}

	}

	@Override
	public void modellingAnswer(long chatId, int msgId, Msg rMessageCommand, SendMessageExc sMessage) {

		if (sMessage.getText() != null && sMessage.getText().startsWith(ProjectNotFoundException.PROJECT_NOT_FOUND)) {
			this.setStandardState(chatId);
			tChannel.sendMessageAndWait(msgId, chatId, sMessage);
		} else {
			this.setState(chatId);
			String text=sMessage.getText();
			sMessage.setText(super.getProjectsFormat(sMessage.getMessage())+"\n\n"+SHOW_MSG);
			String [][] projects =super.getProjects(text);
			tChannel.sendMessageWithKeyBoar(msgId, chatId, sMessage, projects);
		}
	}

	@Override
	public void userAnswerText(Update update) {
		this.setStandardState(update.getMessage().getChatId());
		tChannel.write(update,CommandList.BASE_CASE, update.getMessage().getText(), null, null);
	}

}
