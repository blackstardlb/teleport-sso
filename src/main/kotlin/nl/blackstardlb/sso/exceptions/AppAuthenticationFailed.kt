package nl.blackstardlb.sso.exceptions

class AppAuthenticationFailed(app: String, throwable: Throwable) :
    Exception("Failed to authenticate app $app", throwable)
