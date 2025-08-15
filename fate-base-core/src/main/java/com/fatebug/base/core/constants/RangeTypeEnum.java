package com.fatebug.base.core.constants;

/**
 * 取值范围枚举类
 */
public enum RangeTypeEnum {

	/** 数字（无零） */
	NUMBER_WITHOUT_ZERO(0, "数字（无零）", "123456789"),

	/** 数字 */
	NUMBER_FULL(0, "数字", "0123456789"),

	/** 字母（常用） */
	LETTER_NORMAL(0, "字母（常用）", "qwertasdfgzxcvb"),

	/** 字母（小写） */
	LETTER_FULL_LOWERCASE(0, "字母（小写）", "abcdefghijklmnopqrstuvwxyz"),

	/** 字母（大写） */
	LETTER_FULL_UPPERCASE(0, "字母（大写）", "ABCDEFGHIJKLMNOPQRSTUVWXYZ"),

	/** 字母（大小写） */
	LETTER_FULL(0, "字母（大小写）", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"),

	/** 数字（常用）和字母（常用） */
	NUMBER_NORMAL_AND_LETTER_NORMAL(0, "数字（常用）和字母（常用）", "qwertasdfgzxcvb12345"),

	/** 数字和字母（小写） */
	NUMBER_AND_LETTER_FULL_LOWERCASE(0, "数字和字母（小写）", "0123456789abcdefghijklmnopqrstuvwxyz"),

	/** 数字和字母（大写） */
	NUMBER_AND_LETTER_FULL_UPPERCASE(0, "数字和字母（大写）", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"),

	/** 数字和字母（大小写） */
	NUMBER_AND_LETTER_FULL(0, "数字和字母（大小写）", "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");

	/**
	 * 构造方法
	 * @param code 编码
	 * @param name 中文名称
	 * @param nameEn 英文名称
	 */
	RangeTypeEnum(Integer code, String name, String nameEn) {
		this.code = code;
		this.name = name;
		this.nameEn = nameEn;
	}


	/** 编码 */
	private final Integer code;

	/** 中文名称 */
	private final String name;

	/** 英文名称 */
	private final String nameEn;


	/**
	 * 获取编码
	 * @return 编码
	 */
	public Integer getCode() {
		return this.code;
	}


	/**
	 * 获取中文名称
	 * @return 中文名称
	 */
	public String getName() {
		return this.name;
	}


	/**
	 * 获取英文名称
	 * @return 英文名称
	 */
	public String getNameEn() {
		return this.nameEn;
	}

}
