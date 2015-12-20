A Java implementation of the **"PRECIS Framework: Preparation, Enforcement, and Comparison of Internationalized Strings in Application Protocols"** and profiles thereof.

# Why PRECIS?

**PRECIS** validates and prepares Unicode strings in a way, so that they can safely be used in application protocols, e.g. when dealing with usernames and passwords.

For example, if strings are used for authentication and authorization decisions, the security of an application could be compromised if an entity
providing a given string is connected to the wrong account or online resource based on different interpretations of the string.

**PRECIS** takes care of such issues.

# About

This library supports the following specifications:

* [RFC 7564](https://tools.ietf.org/html/rfc7564): PRECIS Framework: Preparation, Enforcement, and Comparison of Internationalized Strings in Application Protocols
* [RFC 7613](https://tools.ietf.org/html/rfc7613): Preparation, Enforcement, and Comparison of Internationalized Strings Representing Usernames and Passwords
* [RFC 7700](https://tools.ietf.org/html/rfc7700): Preparation, Enforcement, and Comparison of Internationalized Strings Representing Nicknames
* [RFC 5893](https://tools.ietf.org/html/rfc5893): Right-to-Left Scripts for Internationalized Domain Names for Applications (IDNA)

**PRECIS** obsoletes Stringprep ([RFC 3454](https://tools.ietf.org/html/rfc3454)) and this library obsoletes software like [Libidn's Stringprep class](http://www.gnu.org/software/libidn/javadoc/gnu/inet/encoding/Stringprep.html).

This software is licensed under the [MIT license](https://opensource.org/licenses/MIT).

# API & Samples

For most cases, all you need to do is to choose an existing profile from the `PrecisProfiles` class and then prepare or enforce a string:

```java
PrecisProfile profile1 = PrecisProfiles.USERNAME_CASE_MAPPED;
PrecisProfile profile2 = PrecisProfiles.USERNAME_CASE_PRESERVED;
PrecisProfile profile3 = PrecisProfiles.OPAQUE_STRING;
PrecisProfile profile4 = PrecisProfiles.NICKNAME;
```

`PrecisProfile` is an abstract class, which you could derive from for defining your custom profile (which however is [discouraged](https://tools.ietf.org/html/rfc7564#section-5.1) by RFC 7564).

## Preparation

Preparation ensures, that characters are allowed, but (usually) does not apply any mapping rules. The following throws an exception because the string contains a character, which is in the Unicode category *Lt*, which is disallowed.

```java
PrecisProfiles.USERNAME_CASE_MAPPED.prepare("\u01C5"); // CAPITAL LETTER D WITH SMALL LETTER Z WITH CARON
```

## Enforcement

Enforcement applies a set of rules (e.g. Unicode normalization, width-mapping, case-folding, ...) to a string in order to transform it to a canonical form and to compare two strings, e.g. for the purpose of authentication.

```java
String enforced = PrecisProfiles.USERNAME_CASE_MAPPED.enforce("UpperCaseUsername"); // => uppercaseusername
```

Here, only simple mapping to lower case is applied. But enforcement does more:

```java
String ang = PrecisProfiles.USERNAME_CASE_MAPPED.enforce("\u212B");     // ANGSTROM SIGN
String a = PrecisProfiles.USERNAME_CASE_MAPPED.enforce("\u0041\u030A"); // LATIN CAPITAL LETTER A + COMBINING RING ABOVE
String aRing = PrecisProfiles.USERNAME_CASE_MAPPED.enforce("\u00C5");   // LATIN CAPITAL LETTER A WITH RING ABOVE

// ang.equals(a) == true
// a.equals(aRing) == true
```

All three result in `LATIN SMALL LETTER A WITH RING ABOVE` (U+00E5) and are therefore equal after enforcement.

The following throws an exception because it violates the Bidi Rule (RFC 5893).

```java
USERNAME_CASE_MAPPED.enforce("\u0786test");
```

If a string contains prohibited characters, e.g. symbols in usernames, a `PrecisException` is thrown, either during preparation or enforcement.

## Comparison

After enforcement, two strings can simply be compared with the `String.equals()` or `String.compareTo()` method.