### **Changes** ###
* When using the `SQLServer` database handler, string literals containing the character code of `U+0080` and above are now generated with `N` prefix (e.g. `N'漢字'`).
* When using the `SQLite` database handler, literals of `byte[]` are now generated in the `X'hhhhhh'` format if the array length does not exceed the `maxBinaryLiteralLength`.
