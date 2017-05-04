package com.mayacarlsen.util;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.Getter;

public class CodesDAO {

	@Getter
	public static final List<CodeDescription> USER_ROLES = ImmutableList.of(
			new CodeDescription("ADMIN", "Administrator Role"),
			new CodeDescription("USER", "User Role")
		);
	
	@Getter
	public static final List<CodeDescription> PUBLISH = ImmutableList.of(
			new CodeDescription("true", "True - Publish"),
			new CodeDescription("false", "False - Do Not Publish")
		);
}
