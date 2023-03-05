package nl.blackstardlb.sso.exceptions

class UserNotFoundException(userName: String) : Exception("User $userName was not found")
