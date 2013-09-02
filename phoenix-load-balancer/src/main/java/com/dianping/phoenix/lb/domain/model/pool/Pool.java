package com.dianping.phoenix.lb.domain.model.pool;

import java.util.List;

import com.dianping.phoenix.lb.domain.model.StatefulBaseEntity;

public class Pool extends StatefulBaseEntity {

	private String name;
	private List<PoolMember> members;
	private LoadBalancingMethod lbMethod;
	private int minAvailMemberPct;

	public Pool(String name, List<PoolMember> members, LoadBalancingMethod lbMethod, int minAvailMemberPct) {
		this.name = name;
		this.members = members;
		this.lbMethod = lbMethod;
		this.minAvailMemberPct = minAvailMemberPct;
	}

	public void enableMember(long memberId) {
		for (PoolMember member : members) {
			if (member.getId() == memberId) {
				member.enable();
				break;
			}
		}
	}

	public void disableMember(long memberId) {

		PoolMember memberFound = null;
		int availMemberCnt = 0;
		for (PoolMember member : members) {
			if (member.getAvailablity() == Availablity.AVAILABLE) {
				availMemberCnt++;
			}
			if (member.getId() == memberId) {
				memberFound = member;
			}
		}
		if (memberFound != null && isAvailMemberCntValid(availMemberCnt - 1)) {
			memberFound.disable();
		}
	}

	private boolean isAvailMemberCntValid(int availMemberCnt) {
		return 100 * availMemberCnt / members.size() >= minAvailMemberPct;
	}

	public List<PoolMember> getMembers() {
		return members;
	}

	public void setMembers(List<PoolMember> members) {
		this.members = members;
	}

	public LoadBalancingMethod getLbMethod() {
		return lbMethod;
	}

	public void setLbMethod(LoadBalancingMethod lbMethod) {
		this.lbMethod = lbMethod;
	}

	public String getName() {
		return name;
	}

}
