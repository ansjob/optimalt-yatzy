package se.kth.ansjobmarcular;

public class MarkingAction {
	private byte action;
	
	public MarkingAction(int action) {
		this.action = (byte) action;
	}

	public int getIndex() {
		return action;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + action;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MarkingAction other = (MarkingAction) obj;
		if (action != other.action)
			return false;
		return true;
	}
	
	
}
