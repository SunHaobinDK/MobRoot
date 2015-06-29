package com.mob.root.net.parser;

public abstract class Parser {

	public abstract <T> T parse(String datas) throws Exception;
}
