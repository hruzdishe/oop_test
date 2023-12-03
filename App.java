package com.hruzd;

import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
interface ButtonAction {
	
	void run();
}

final class TextUtils {
	
	private TextUtils() {
	}
	
	private static String getLine(int length) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < length + 4; ++i) {
			sb.append('-');
		}
		
		return sb.toString();
	}
	
	public static String getTextBlock(String text) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(getLine(text.length()));
		sb.append('\n');
		sb.append("| ");
		sb.append(text);
		sb.append(" |");
		sb.append('\n');
		sb.append(getLine(text.length()));
		
		return sb.toString();
	}
}

class Button {
	
	private String text;
	private ButtonAction buttonAction;
	
	public Button(String text) {
		this.text = text;
	}
	
	public Button(String text, ButtonAction buttonAction) {
		this.text = text;
		this.buttonAction = buttonAction;
	}
	
	public String getText() {
		return text;
	}
	
	public ButtonAction getButtonAction() {
		return buttonAction;
	}
	
	public void setButtonAction(ButtonAction buttonAction) {
		this.buttonAction = buttonAction;
	}
	
	@Override
	public String toString() {
		return TextUtils.getTextBlock("Button{" + text + "}");
	}
}

class InlineButton extends Button {

	public InlineButton(String text) {
		super(text);
	}
	
	public InlineButton(String text, ButtonAction buttonAction) {
		super(text, buttonAction);
	}
	
	@Override
	public String toString() {
		return TextUtils.getTextBlock("Inline{" + getText() + "}");
	}
}

abstract class TelegramKeyboard<T extends Button> {

	protected List<List<T>> buttons;
	
	protected TelegramKeyboard() {
		buttons = new ArrayList<>(new ArrayList<>());
	}
	
	public List<List<T>> getButtons() {
		return buttons;
	}
	
	public void print() {
		int i = 0;
		for (List<T> row : buttons) {
			System.out.println("{ROW" + (++i) + "}");
			for (T button : row) {
				System.out.println(button);
			}
		}
	}
}

interface Builder<T extends Button> {
	
	void add(T button);
	
	void addAll(List<T> buttons);
	
	void newLine();
	
	TelegramKeyboard<T> build();
}

class InlineKeyboard extends TelegramKeyboard<InlineButton> {
	
	private InlineKeyboard(List<List<InlineButton>> buttons) {
		super.buttons = buttons;
	}
	
	public static Builder<InlineButton> builder() {
		return new KeyboardBuilder();
	}
	
	private static class KeyboardBuilder implements Builder<InlineButton> {

		private List<List<InlineButton>> buttons;
		private List<InlineButton> row;
		
		public KeyboardBuilder() {
			buttons = new ArrayList<>(new ArrayList<>());
			row = new ArrayList<>(new ArrayList<>());
		}
		
		@Override
		public void add(InlineButton button) {
			row.add(button);
		}

		@Override
		public void addAll(List<InlineButton> buttons) {
			row.addAll(buttons);
		}

		@Override
		public void newLine() {
			buttons.add(List.copyOf(row));
			row.clear();
		}

		@Override
		public TelegramKeyboard<InlineButton> build() {
			return new InlineKeyboard(buttons);
		}
		
	}
}

class ReplyKeyboard extends TelegramKeyboard<Button> {

	private ReplyKeyboard(List<List<Button>> buttons) {
		super.buttons = buttons;
	}
	
	public static Builder<Button> builder() {
		return new KeyboardBuilder();
	}
	
	private static class KeyboardBuilder implements Builder<Button> {

		private List<Button> row;
		private List<List<Button>> buttons;
		
		public KeyboardBuilder() {
			row = new ArrayList<>();
			buttons = new ArrayList<>(new ArrayList<>());
		}
		
		@Override
		public void add(Button button) {
			row.add(button);
		}

		@Override
		public void addAll(List<Button> buttons) {
			row.addAll(buttons);
		}

		@Override
		public void newLine() {
			buttons.add(List.copyOf(row));
			row.clear();
		}

		@Override
		public TelegramKeyboard<Button> build() {
			return new ReplyKeyboard(buttons);
		}
		
	}
}

public class App {
	
	@SuppressWarnings("unused")
	private static void printDefaultKeyboard() {
		Builder<Button> builder = ReplyKeyboard.builder();
		builder.add(new Button("First Button"));
		builder.add(new Button("Second Button"));
		builder.newLine();
		builder.add(new Button("Third Button"));
		builder.add(new Button("Fourth Button"));
		builder.newLine();
		
		TelegramKeyboard<Button> defaultKb = builder.build();
		defaultKb.print();		
	}
	
	@SuppressWarnings("unused")
	private static void printInlineKeyboard() {
		Builder<InlineButton> builder = InlineKeyboard.builder();
		builder.add(new InlineButton("Hello"));
		builder.add(new InlineButton("Test"));
		builder.newLine();
		builder.add(new InlineButton("World"));
		builder.add(new InlineButton("Test"));
		builder.add(new InlineButton("Test"));
		builder.add(new InlineButton("Test"));
		builder.newLine();
		
		TelegramKeyboard<InlineButton> kb = builder.build();
		kb.print();
	}
	
	public static void main(String[] args) {
		
	}
}
