/*
	Accessor.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.helper;

import java.lang.reflect.Field;
import java.util.List;

/**
	オブジェクトのフィールドに対して値の取得と設定を行います。

	@param <T> 対象オブジェクトの型

	@since 1.0.0
	@author Masato Kokubo
*/
public class Accessor<T> {
	/**
		<b>Accessor</b> を構築します。

		@param objectClass アクセス対象のオブジェクトのクラス

		@throws NullPointerException <b>objectClass</b> が null の場合
	*/
	public Accessor(Class<T> objectClass) {
	}

	/**
		アクセス可能な全フィールドのプロパティ名のリストを返します。
		ネストしていない場合、プロパティ名は、フィールド名と同じです。
		ネストしている場合は、各フィールドをピリオドでつなげた名前になります。(例 <b>name.first</b>)

		@return プロパティ名のリスト
	*/
	public List<String> propertyNames() {
		return null;
	}

	/**
		アクセス可能な値型のフィールドのプロパティ名のリストを返します。<br>
		ネストしていない場合、プロパティ名は、フィールド名と同じです。
		ネストしている場合は、各フィールドをピリオドでつなげた名前になります。(例 <b>name.first</b>)<br>

		値型は以下のいずれかです。<br>

		<div class="blankline">&nbsp;</div>

		<div class="code indent">
			boolean, char, byte, short, int, long, float, double,<br>
			Boolean, Character, Byte, Short, Integer, Long, Float, Double, BigInteger, BigDecimal,<br>
			String, java.util.Date, java.sql.Date, Time, Timestamp
		</div>

		@return 値型のプロパティ名のリスト
	*/
	public List<String> valuePropertyNames() {
		return null;
	}

	/**
		<b>propertyName</b> で指定されるフィールドの <b>Field</b> オブジェクトを返します。

		@param propertyName フィールドのプロパティ名

		@return Field オブジェクト

		@throws IllegalArgumentException <b>propertyName</b> で指定されるフィールドが見つからない場合
	*/
	public Field getField(String propertyName) {
		return null;
	}

	/**
		<b>propertyName</b> で指定されるフィールドの型を返します。

		@param propertyName フィールドのプロパティ名

		@return フィールドの型

		@throws IllegalArgumentException <b>propertyName</b> で指定されるフィールドが見つからない場合
	*/
	public Class<?> getType(String propertyName) {
		return null;
	}

	/**
		指定のオブジェクトのフィールドの値を返します。<br>
		<b>public</b> フィールドの場合は、直接値を取得しますが、
		非<b>public</b> フィールドの場合は、<b>public</b> な取得メソッドを使用します。<br>
		フィールド名が <b>foo</b> の場合、取得メソッドは、以下のいずれかです。<br>
		<ul>
			<li><b>foo()   </b></li>
			<li><b>getFoo()</b></li>
			<li><b>isFoo() </b></li>
		</ul>

		@param object 対象のオブジェクト
		@param propertyName フィールドのプロパティ名

		@return フィールドから取得した値 (null 有)

		@throws NullPointerException <b>object</b> が null の場合
		@throws IllegalArgumentException <b>propertyName</b> で指定されるフィールドの取得メソッドが見つからない場合
		@throws RuntimeException <b>IllegalAccessException</b> または <b>InvocationTargetException</b> がスローされた場合
	*/
	public Object getValue(T object, String propertyName) {
		return null;
	}

	/**
		指定のオブジェクトのフィールドに値を設定します。<br>
		<b>public</b> フィールドの場合は、直接値を設定しますが、
		非<b>public</b> フィールドの場合は、<b>public</b> な設定メソッドを使用します。<br>
		フィールド名が <b>foo</b> の場合、設定メソッドは、以下のいずれかです。<br>
		<ul>
			<li><b>foo()   </b></li>
			<li><b>setFoo()</b></li>
		</ul>

		@param object 対象のオブジェクト
		@param propertyName フィールドのプロパティ名
		@param value フィールドに設定する値 (null 可)

		@throws NullPointerException <b>object</b> が null の場合
		@throws IllegalArgumentException <b>propertyName</b> で指定されるフィールドの設定メソッドが見つからない場合
		@throws RuntimeException <b>IllegalAccessException</b> または <b>InvocationTargetException</b> がスローされた場合
	*/
	public void setValue(T object, String propertyName, Object value) {
	}
}
