# Aeminium - identity

**Summary**

This is a REST webservice, developed in JakartaEE/MicroProfile, whose purpose is:
* generate JWT token if the user's identity id validated
* add/remove users
* edit user details, such as roles

**Development details**

This app is develped using the following technologies:
* JDK 21
* Maven 3.9.8
* JakartaEE 10 and MicroProfile 6.1
* Payara Micro as the runtime

Some extra resources are included:
* a Docker image for the creation of containers of this app
* a development RSA private key used to sign development JWT tokens
* a development RSA public key used to validate authorisation requests to resources 

***

Documentation for this project can be found here: [Aeminium Notion Page](https://cmogoncalves.notion.site/Aeminium-0e21f43b2a944791828e15d5252ddc84?pvs=4)