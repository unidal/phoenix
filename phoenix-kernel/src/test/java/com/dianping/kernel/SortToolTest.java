package com.dianping.kernel;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.kernel.plugin.SortTool;
import com.dianping.kernel.plugin.SortTool.SortElement;

public class SortToolTest {
	
	private List<SortElement> sortElementList = new ArrayList<SortElement>();
	private List<SortElement> expectedElementList = new ArrayList<SortElement>();
	
	SortElementTestImpl app1 = new SortElementTestImpl(null,"app1","com.dianping.app1");
	SortElementTestImpl app2 = new SortElementTestImpl(null,"app2","com.dianping.app2");
	SortElementTestImpl app3 = new SortElementTestImpl(null,"app3","com.dianping.app3");
	
	SortElementTestImpl uncertain1 = new SortElementTestImpl("-:Null|-com.dianping.Null|+1","uncertain1","com.dianping.uncertain1");
	
	SortElementTestImpl before4 = new SortElementTestImpl("+4","before4","com.dianping.before4");
	SortElementTestImpl before2 = new SortElementTestImpl("+2","before2","com.dianping.before2");
	SortElementTestImpl before3 = new SortElementTestImpl("+2","before3","com.dianping.before3");
	SortElementTestImpl before1 = new SortElementTestImpl("+1","before1","com.dianping.before1");
	
	SortElementTestImpl after4 = new SortElementTestImpl("-3","after4","com.dianping.after4");
	SortElementTestImpl after2 = new SortElementTestImpl("-2","after2","com.dianping.after2");
	SortElementTestImpl after3 = new SortElementTestImpl("-2","after3","com.dianping.after3");
	SortElementTestImpl after1 = new SortElementTestImpl("-1","after1","com.dianping.after1");
	
	SortElementTestImpl uncertain2 = new SortElementTestImpl("-com.dianping.Null|+1","uncertain2","com.dianping.uncertain2");
	SortElementTestImpl uncertain3 = new SortElementTestImpl("-:app1|-com.dianping.Null|+1","uncertain3","com.dianping.uncertain3");
	SortElementTestImpl uncertain4 = new SortElementTestImpl("+com.dianping.app2|+1","uncertain4","com.dianping.uncertain4");
	SortElementTestImpl uncertain5 = new SortElementTestImpl("-com.dianping.app3","uncertain5","com.dianping.uncertain5");
	SortElementTestImpl uncertain6 = new SortElementTestImpl("-:app3","uncertain6","com.dianping.uncertain6");
	SortElementTestImpl uncertain7 = new SortElementTestImpl("+:app3","uncertain7","com.dianping.uncertain7");
	
	SortElementTestImpl platform = new SortElementTestImpl(null,"platform","com.dianping.platform");
	SortElementTestImpl uncertain_1 = new SortElementTestImpl("+:platform","uncertain_1","com.dianping.uncertain_1");
	
	SortElementTestImpl uncertain_2 = new SortElementTestImpl("+:after3","uncertain_2","com.dianping.uncertain_2");
	
	@Before
	public void init(){
		sortElementList.add(app1);
		sortElementList.add(app2);
		sortElementList.add(app3);
		
	}
	
	@After
	public void clear(){
		this.sortElementList.clear();
		this.expectedElementList.clear();
		
	}
	
	@Test
	public void indexSortTest(){
		sortElementList.add(before2);
		sortElementList.add(before3);
		
		expectedElementList.add(before2);
		expectedElementList.add(before3);
		expectedElementList.add(app1);
		expectedElementList.add(app2);
		expectedElementList.add(app3);
		
		List<SortElement>  sel = new SortTool().sort(sortElementList);
		Assert.assertArrayEquals(expectedElementList.toArray(), sel.toArray());
	}
	
	@Test
	public void beforeSortTest(){
		sortElementList.add(before4);
		sortElementList.add(before2);
		sortElementList.add(before3);
		sortElementList.add(before1);
		
		expectedElementList.add(before1);
		expectedElementList.add(before2);
		expectedElementList.add(before3);
		expectedElementList.add(before4);
		expectedElementList.add(app1);
		expectedElementList.add(app2);
		expectedElementList.add(app3);
		
		List<SortElement>  sel = new SortTool().sort(sortElementList);
		Assert.assertArrayEquals(expectedElementList.toArray(), sel.toArray());
	}
	
	@Test
	public void afterSortTest(){
		sortElementList.add(after4);
		sortElementList.add(after2);
		sortElementList.add(after3);
		sortElementList.add(after1);
		
		expectedElementList.add(app1);
		expectedElementList.add(app2);
		expectedElementList.add(app3);
		expectedElementList.add(after1);
		expectedElementList.add(after2);
		expectedElementList.add(after3);
		expectedElementList.add(after4);
		
		List<SortElement>  sel = new SortTool().sort(sortElementList);
		Assert.assertArrayEquals(expectedElementList.toArray(), sel.toArray());
	}
	
	@Test
	public void sortByNameTest(){
		sortElementList.add(uncertain6);
		sortElementList.add(uncertain7);
		
		expectedElementList.add(app1);
		expectedElementList.add(app2);
		expectedElementList.add(uncertain6);
		expectedElementList.add(app3);
		expectedElementList.add(uncertain7);
		
		List<SortElement>  sel = new SortTool().sort(sortElementList);
		Assert.assertArrayEquals(expectedElementList.toArray(), sel.toArray());
	}
	
	@Test
	public void sortByClassNameTest(){
		sortElementList.add(uncertain5);
		
		expectedElementList.add(app1);
		expectedElementList.add(app2);
		expectedElementList.add(uncertain5);
		expectedElementList.add(app3);
		
		
		List<SortElement>  sel = new SortTool().sort(sortElementList);
		Assert.assertArrayEquals(expectedElementList.toArray(), sel.toArray());
	}
	
	@Test
	public void sortDiscardTest(){
		sortElementList.add(uncertain5);
		
		expectedElementList.add(app1);
		expectedElementList.add(app2);
		expectedElementList.add(uncertain5);
		expectedElementList.add(app3);
		
		
		List<SortElement>  sel = new SortTool().sort(sortElementList);
		Assert.assertArrayEquals(expectedElementList.toArray(), sel.toArray());
	}
	
	@Test
	public void MultiRuleSortTest(){
		sortElementList.add(uncertain1);
		sortElementList.add(uncertain2);
		sortElementList.add(uncertain3);
		sortElementList.add(uncertain4);
		
		expectedElementList.add(uncertain1);
		expectedElementList.add(uncertain2);
		expectedElementList.add(uncertain3);
		expectedElementList.add(app1);
		expectedElementList.add(app2);
		expectedElementList.add(uncertain4);
		expectedElementList.add(app3);
		
		List<SortElement>  sel = new SortTool().sort(sortElementList);
		Assert.assertArrayEquals(expectedElementList.toArray(), sel.toArray());
	}
	
	@Test
	public void complexSortTest(){
		//Need to sort the data
		sortElementList.add(uncertain1);
		
		sortElementList.add(before4);
		sortElementList.add(before2);
		sortElementList.add(before3);
		sortElementList.add(before1);
		
		sortElementList.add(after4);
		sortElementList.add(after2);
		sortElementList.add(after3);
		sortElementList.add(after1);
		
		sortElementList.add(uncertain2);
		
		sortElementList.add(uncertain3);
		sortElementList.add(uncertain4);
		sortElementList.add(uncertain5);
		sortElementList.add(uncertain6);
		sortElementList.add(uncertain7);
		
		sortElementList.add(platform);
		sortElementList.add(uncertain_1);
		sortElementList.add(uncertain_2);
		//expected data
		expectedElementList.add(uncertain1);
		expectedElementList.add(before1);
		expectedElementList.add(uncertain2);
		expectedElementList.add(before2);
		expectedElementList.add(before3);
		expectedElementList.add(before4);
		expectedElementList.add(uncertain3);
		expectedElementList.add(app1);
		expectedElementList.add(app2);
		expectedElementList.add(uncertain4);
		expectedElementList.add(uncertain5);
		expectedElementList.add(uncertain6);
		expectedElementList.add(app3);
		expectedElementList.add(uncertain7);
		expectedElementList.add(platform);
		expectedElementList.add(uncertain_1);
		expectedElementList.add(after1);
		expectedElementList.add(after2);
		expectedElementList.add(after3);
		expectedElementList.add(after4);
		
		List<SortElement>  sel = new SortTool().sort(sortElementList);
		Assert.assertArrayEquals(expectedElementList.toArray(), sel.toArray());
	}
	
	
	private static class SortElementTestImpl implements SortElement{
		
		private String rule;
		private String name;
		private String className;
		
		public SortElementTestImpl(String rule,String name,String className) {
			this.rule = rule;
			this.name = name;
			this.className = className;
		}

		@Override
		public String getRule() {
			// TODO Auto-generated method stub
			return this.rule;
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return this.name;
		}

		@Override
		public String getClassName() {
			// TODO Auto-generated method stub
			return this.className;
		}
		
	}

	public static void main(String[] args){
		String ff = "fsdf|ggfg";
		int idx = ff.indexOf("|");
			System.out.println(ff.substring(0,idx));
			System.out.println(ff.substring(idx+1));
	}
}
