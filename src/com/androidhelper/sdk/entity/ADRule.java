package com.androidhelper.sdk.entity;

import com.androidhelper.sdk.ADType;
import com.androidhelper.sdk.EventType;

public class ADRule implements Comparable<ADRule> {

	/** 触发事件类型 */
	public EventType eventType;

	/** 广告展现形式 */
	public ADType adtype;

	/** 展现概率 */
	public float probability;

	/** 同类广告展现频次 */
	public int freqSameCat;

	/** 任意广告展现频次 */
	public int freqGlobal;

	/** 优先级 */
	public int level;

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public ADType getAdtype() {
		return adtype;
	}

	public void setAdtype(ADType adtype) {
		this.adtype = adtype;
	}

	public float getProbability() {
		return probability;
	}

	public void setProbability(float probability) {
		this.probability = probability;
	}

	public int getFreqSameCat() {
		return freqSameCat;
	}

	public void setFreqSameCat(int freqSameCat) {
		this.freqSameCat = freqSameCat;
	}

	public int getFreqGlobal() {
		return freqGlobal;
	}

	public void setFreqGlobal(int freqGlobal) {
		this.freqGlobal = freqGlobal;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public int compareTo(ADRule another) {
		return another.getLevel() - level;
	}
}
