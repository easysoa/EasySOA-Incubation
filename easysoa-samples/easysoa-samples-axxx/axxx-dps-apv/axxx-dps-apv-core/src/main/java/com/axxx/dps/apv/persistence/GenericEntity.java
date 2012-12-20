package com.axxx.dps.apv.persistence;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
public abstract class GenericEntity<T> implements Comparable<GenericEntity<T>>, Serializable {

	protected final static Logger LOG = Logger.getLogger(GenericEntity.class.getCanonicalName());

	private static final long serialVersionUID = -3143481734054908460L;
	
	protected static final int COMPARE_LESS = -1;
	protected static final int COMPARE_EQUALS = 0;
	protected static final int COMPARE_GREATER = 1;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public boolean isNew() {
		return this.getId() == null;
	}

	public int compareTo(GenericEntity<T> o) {
		if (this.isNew()) {
			return COMPARE_LESS;
		}
		if (o.isNew()) {
			return COMPARE_GREATER;
		}
		return this.getId().compareTo(o.getId());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!this.getClass().isInstance(o)) {
			return false;
		}
		GenericEntity<?> oge = (GenericEntity<?>) o;
		if (this.isNew() || oge.isNew()) {
			return false;
		}
		return this.getId().equals(oge.getId());
	}

	@Override
	public int hashCode() {
		return (this.isNew()) ? super.hashCode() : this.getId().hashCode();
	}
	
}