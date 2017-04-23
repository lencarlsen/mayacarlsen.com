package com.mayacarlsen.security;

import java.util.Iterator;
import java.util.List;

import com.mayacarlsen.user.User;
import com.mayacarlsen.user.UserRoleEnum;
import com.mayacarlsen.util.AuthObject;

public class AuthorizedList<T extends AuthObject> {

	private User user;
	private List<T> list;
	
	/**
	 * Create a filtered list the <code>user</code> is authorised to access.
	 * List items the <code>user</code> is not allowed to access are removed.
	 * 
	 * @param user User to authorise the list against
	 * @param list List of items
	 */
	public AuthorizedList(User user, List<T> list) {
		this.user = user;
		this.list = list;
	}
	
	private void filter() {
		UserRoleEnum role = UserRoleEnum.valueOf(user.getRole());
		if (role != UserRoleEnum.ADMIN) {
			Iterator<T> it = list.iterator();
			while(it.hasNext()) {
				AuthObject obj = it.next();
				if (!obj.getUser_id().equals(this.user.getUser_id())) {
					it.remove();
				}
			}
		}
	}
	
	/**
	 * Gets authorised list the user is allowed to access. 
	 * @return List of authorised items
	 */
	public List<T> getList() {
		filter();
		return list;
	}
}
