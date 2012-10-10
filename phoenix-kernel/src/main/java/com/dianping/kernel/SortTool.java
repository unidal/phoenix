package com.dianping.kernel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bin.miao
 *
 */
public class SortTool {
	
	private static final String ruleDelimiter = "|";
	private static final String beforeOperator = "-";
	private static final String afterOperator = "+";
	private static final String nameFlag = ":";
	
	private static final int getElementByName = 1;
	private static final int getElementByClassName = 2;
	
	public static List<SortElement> sort(List<SortElement> elementList){
		
		List<SortElementWrapper> beforeWrapperList = new ArrayList<SortElementWrapper>();
		List<SortElementWrapper> duringWrapperList = new ArrayList<SortElementWrapper>();
		List<SortElementWrapper> afterWrapperList = new ArrayList<SortElementWrapper>();
		List<SortElementWrapper> uncertainWrapperList = new ArrayList<SortElementWrapper>();
		
		if(elementList != null && elementList.size() > 0){
			for(int i=0;i<elementList.size();i++){
				
				SortElement element = elementList.get(i);
				String rule = element.getRule();
				
				SortElementWrapper wrapper = new SortElementWrapper(i,element);
				
				
				
			}
			
		}
		return null;
	}
	
	private static void segment(String rule_,SortElementWrapper wrapper,
			List<SortElement> elementList,
			List<SortElementWrapper> beforeWrapperList,
			List<SortElementWrapper> duringWrapperList,
			List<SortElementWrapper> afterWrapperList,
			List<SortElementWrapper> uncertainWrapperList){
		String rule = rule_.trim();
		if(rule == null || rule.length() == 0 ||rule.equals("0")){
			//init-param of "_INDEX_" is null/""/"0"
			duringWrapperList.add(wrapper);
		}else{
			try{
				int position = Integer.parseInt(rule);
				if(position > 0){
					//rule : "+1/2/3..."
					beforeWrapperList.add(wrapper);
				}else if(position < 0){
					//rule : "-1/2/3..."
					afterWrapperList.add(wrapper);
				}
				wrapper.setRule(position);
			}catch(Exception e){
				//Not just numbers rule
				if(rule.indexOf(ruleDelimiter) > -1){
					
				}else{
					uncertainWrapperList.add(wrapper);
					wrapper.setRule(rule);
				}
			}
		}
	}
	
	private static String getEffectiveRule(String rule,List<SortElement> elementList){
		String[] rules = rule.split(ruleDelimiter);
		for(int i=0;i<rules.length;i++){
			String r = rules[i];
			if(r != null&&r.length() > 0){
				try{
					Integer.parseInt(r);
					return r;
				}catch(Exception e){
					if(r.indexOf(nameFlag) > 0){
						String[] params = r.split(nameFlag);
						
					}else{
						
					}
				}
			}
		}
		return null;
	}
	
	private static SortElement getElement(String param,SortElement self,
			List<SortElement> elementList,int getElementBy){
		
		for(SortElement element : elementList){
			if(getElementByName == getElementBy 
					&& element.getName().equals(param)
					&& element != self){
				return element;
			}
			if(getElementByClassName == getElementBy 
					&& element.getClassName().equals(param)
					&& element != self){
				return element;
			}
		}
		return null;
	}
	
	/**
	 * @author bin.miao
	 *
	 */
	public static interface SortElement{
		public String getRule();
		public String getName();
		public String getClassName();
	}
	
	/**
	 * 
	 * @author bin.miao
	 *
	 */
	private static class SortElementWrapper{
		
		private int idx = 0;
		private SortElement element;
		
		private boolean isRuleNumber = false;
		private String ruleStr = null;
		private int ruleNum = 0;
		
		SortElementWrapper(int idx,SortElement element){
			this.idx = idx;
			this.element = element;
		}
		
		public void setRule(int ruleNum){
			this.ruleNum = ruleNum;
			this.isRuleNumber = true;
		}
		
		public int getRuleNum(){
			return this.ruleNum;
		}
		
		public void setRule(String ruleStr){
			this.ruleStr = ruleStr;
			this.isRuleNumber = false;
		}
		
		public String getRuleStr(){
			return this.ruleStr;
		}
		
		public boolean isRuleNumber(){
			return this.isRuleNumber;
		}

		public int getIdx() {
			return idx;
		}

		public void setIdx(int idx) {
			this.idx = idx;
		}

		public SortElement getElement() {
			return element;
		}

		public void setElement(SortElement element) {
			this.element = element;
		}
		
	}

}
