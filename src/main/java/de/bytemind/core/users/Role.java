package de.bytemind.core.users;

/**
 * User roles for account. Certain activities require certain roles.
 * 
 * @author Daniel, Florian
 *
 */
public enum Role {
	unknown,
	developer, seniordev, chiefdev,
	tester,
	translator,
	user, superuser,
	assistant,
	thing 			//e.g. for IoT devices
}