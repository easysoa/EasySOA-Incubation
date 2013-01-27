package proto.bdaccess;

/**
 * an enumeration to modelised Business service level.
 * @author Florent
 *
 */
public enum BusinessServiceLevel {
	//gold service level for the best level of service according to the sla
	GOLD {
		@Override
		public boolean isWorse(BusinessServiceLevel sl) {
			
			return false;
		}
	},
	//silver service level is medium service level
	SILVER {
		@Override
		public boolean isWorse(BusinessServiceLevel sl) {
			
			return  sl.equals(GOLD);
		}
	} ,
	//bronze service level for the least we could expect from our provider
	BRONZE {
		@Override
		public boolean isWorse(BusinessServiceLevel sl) {
			
			return sl.equals(SILVER)||sl.equals(GOLD);
		}
	} ,
	// violation of the sla.
	VIOLATION {
		@Override
		public boolean isWorse(BusinessServiceLevel sl) {
			
			return true;
		}
	} ;
/**
 * compare two service level 
 * tell if the given service level is worse
 * @param sl
 * @return
 */
	abstract public boolean isWorse(BusinessServiceLevel sl);
	



}
