package com.winrescue.usb

/**
 * Singleton de mapping entre caracteres/touches et rapports HID USB.
 *
 * Supporte les layouts QWERTY US et AZERTY FR.
 * Un rapport HID clavier fait 8 octets :
 *   [0] modifier mask, [1] reserved (0x00), [2-7] keycodes (max 6 simultanes)
 */
object HidKeyMap {

    // ---------------------------------------------------------------
    // Layout enum
    // ---------------------------------------------------------------

    enum class KeyboardLayout(val label: String) {
        QWERTY_US("QWERTY US"),
        AZERTY_FR("AZERTY FR")
    }

    // ---------------------------------------------------------------
    // Modificateurs
    // ---------------------------------------------------------------

    private val modifierMap: Map<String, Byte> = mapOf(
        "CTRL" to 0x01.toByte(),
        "CONTROL" to 0x01.toByte(),
        "LCTRL" to 0x01.toByte(),
        "SHIFT" to 0x02.toByte(),
        "LSHIFT" to 0x02.toByte(),
        "ALT" to 0x04.toByte(),
        "LALT" to 0x04.toByte(),
        "META" to 0x08.toByte(),
        "WIN" to 0x08.toByte(),
        "GUI" to 0x08.toByte(),
        "LMETA" to 0x08.toByte(),
        "LWIN" to 0x08.toByte(),
        "LGUI" to 0x08.toByte(),
        "RCTRL" to 0x10.toByte(),
        "RSHIFT" to 0x20.toByte(),
        "RALT" to 0x40.toByte(),
        "ALTGR" to 0x40.toByte(),
        "RMETA" to 0x80.toByte(),
        "RWIN" to 0x80.toByte(),
        "RGUI" to 0x80.toByte(),
    )

    /**
     * Convertit un nom de modificateur en son masque HID.
     * @return le masque (1 bit parmi 8) ou 0x00 si inconnu.
     */
    fun modifierToByte(modifier: String): Byte =
        modifierMap[modifier.uppercase().trim()] ?: 0x00

    // ---------------------------------------------------------------
    // Touches speciales (scancodes)
    // ---------------------------------------------------------------

    private val specialKeyMap: Map<String, Byte> = mapOf(
        "ENTER" to 0x28.toByte(),
        "RETURN" to 0x28.toByte(),
        "ESCAPE" to 0x29.toByte(),
        "ESC" to 0x29.toByte(),
        "BACKSPACE" to 0x2A.toByte(),
        "TAB" to 0x2B.toByte(),
        "SPACE" to 0x2C.toByte(),
        "DELETE" to 0x4C.toByte(),
        "DEL" to 0x4C.toByte(),
        "INSERT" to 0x49.toByte(),
        "HOME" to 0x4A.toByte(),
        "END" to 0x4D.toByte(),
        "PAGE_UP" to 0x4B.toByte(),
        "PGUP" to 0x4B.toByte(),
        "PAGE_DOWN" to 0x4E.toByte(),
        "PGDN" to 0x4E.toByte(),
        "UP" to 0x52.toByte(),
        "DOWN" to 0x51.toByte(),
        "LEFT" to 0x50.toByte(),
        "RIGHT" to 0x4F.toByte(),
        "F1" to 0x3A.toByte(),
        "F2" to 0x3B.toByte(),
        "F3" to 0x3C.toByte(),
        "F4" to 0x3D.toByte(),
        "F5" to 0x3E.toByte(),
        "F6" to 0x3F.toByte(),
        "F7" to 0x40.toByte(),
        "F8" to 0x41.toByte(),
        "F9" to 0x42.toByte(),
        "F10" to 0x43.toByte(),
        "F11" to 0x44.toByte(),
        "F12" to 0x45.toByte(),
        "CAPS_LOCK" to 0x39.toByte(),
        "PRINT_SCREEN" to 0x46.toByte(),
        "SCROLL_LOCK" to 0x47.toByte(),
        "PAUSE" to 0x48.toByte(),
        "NUM_LOCK" to 0x53.toByte(),
    )

    /**
     * Convertit un nom de touche speciale en scancode HID.
     * @return le scancode ou null si inconnu.
     */
    fun keyNameToCode(key: String): Byte? =
        specialKeyMap[key.uppercase().trim()]

    // ---------------------------------------------------------------
    // Caractere -> rapport HID 8 bytes
    // ---------------------------------------------------------------

    /**
     * Convertit un caractere en rapport HID complet (8 octets).
     * @return le rapport ou null si le caractere n'est pas supportable.
     */
    fun charToReport(
        char: Char,
        layout: KeyboardLayout = KeyboardLayout.QWERTY_US
    ): ByteArray? {
        val (modifier, keyCode) = charToHid(char, layout) ?: return null
        return ByteArray(8).apply {
            this[0] = modifier
            this[2] = keyCode
        }
    }

    /**
     * Dispatch vers le layout approprie.
     */
    private fun charToHid(char: Char, layout: KeyboardLayout): Pair<Byte, Byte>? =
        when (layout) {
            KeyboardLayout.QWERTY_US -> charToHidQwerty(char)
            KeyboardLayout.AZERTY_FR -> charToHidAzerty(char)
        }

    // ---------------------------------------------------------------
    // QWERTY US
    // ---------------------------------------------------------------

    private fun charToHidQwerty(char: Char): Pair<Byte, Byte>? {
        // Lettres minuscules a-z
        if (char in 'a'..'z') {
            val code = (0x04 + (char - 'a')).toByte()
            return Pair(0x00.toByte(), code)
        }
        // Lettres majuscules A-Z
        if (char in 'A'..'Z') {
            val code = (0x04 + (char - 'A')).toByte()
            return Pair(0x02.toByte(), code) // SHIFT
        }
        // Chiffres 0-9
        if (char in '1'..'9') {
            val code = (0x1E + (char - '1')).toByte()
            return Pair(0x00.toByte(), code)
        }
        if (char == '0') {
            return Pair(0x00.toByte(), 0x27.toByte())
        }

        // Caracteres speciaux et symboles
        return when (char) {
            '\n' -> Pair(0x00.toByte(), 0x28.toByte())
            '\t' -> Pair(0x00.toByte(), 0x2B.toByte())
            ' '  -> Pair(0x00.toByte(), 0x2C.toByte())
            '-'  -> Pair(0x00.toByte(), 0x2D.toByte())
            '_'  -> Pair(0x02.toByte(), 0x2D.toByte())
            '='  -> Pair(0x00.toByte(), 0x2E.toByte())
            '+'  -> Pair(0x02.toByte(), 0x2E.toByte())
            '['  -> Pair(0x00.toByte(), 0x2F.toByte())
            '{'  -> Pair(0x02.toByte(), 0x2F.toByte())
            ']'  -> Pair(0x00.toByte(), 0x30.toByte())
            '}'  -> Pair(0x02.toByte(), 0x30.toByte())
            '\\' -> Pair(0x00.toByte(), 0x31.toByte())
            '|'  -> Pair(0x02.toByte(), 0x31.toByte())
            ';'  -> Pair(0x00.toByte(), 0x33.toByte())
            ':'  -> Pair(0x02.toByte(), 0x33.toByte())
            '\'' -> Pair(0x00.toByte(), 0x34.toByte())
            '"'  -> Pair(0x02.toByte(), 0x34.toByte())
            '`'  -> Pair(0x00.toByte(), 0x35.toByte())
            '~'  -> Pair(0x02.toByte(), 0x35.toByte())
            ','  -> Pair(0x00.toByte(), 0x36.toByte())
            '<'  -> Pair(0x02.toByte(), 0x36.toByte())
            '.'  -> Pair(0x00.toByte(), 0x37.toByte())
            '>'  -> Pair(0x02.toByte(), 0x37.toByte())
            '/'  -> Pair(0x00.toByte(), 0x38.toByte())
            '?'  -> Pair(0x02.toByte(), 0x38.toByte())
            // Symboles sur la rangee des chiffres (avec Shift)
            '!'  -> Pair(0x02.toByte(), 0x1E.toByte())
            '@'  -> Pair(0x02.toByte(), 0x1F.toByte())
            '#'  -> Pair(0x02.toByte(), 0x20.toByte())
            '$'  -> Pair(0x02.toByte(), 0x21.toByte())
            '%'  -> Pair(0x02.toByte(), 0x22.toByte())
            '^'  -> Pair(0x02.toByte(), 0x23.toByte())
            '&'  -> Pair(0x02.toByte(), 0x24.toByte())
            '*'  -> Pair(0x02.toByte(), 0x25.toByte())
            '('  -> Pair(0x02.toByte(), 0x26.toByte())
            ')'  -> Pair(0x02.toByte(), 0x27.toByte())
            else -> null
        }
    }

    // ---------------------------------------------------------------
    // AZERTY FR
    // ---------------------------------------------------------------

    /**
     * Mapping AZERTY FR.
     *
     * En AZERTY, les positions physiques des touches sont les memes qu'en QWERTY
     * mais les caracteres qui y sont assignes changent. Le scancode HID correspond
     * a la position physique (QWERTY), donc pour taper 'a' sur un PC configure
     * en AZERTY, il faut envoyer le scancode de la touche physique ou se trouve
     * le 'a' en AZERTY, c'est-a-dire la position du 'q' en QWERTY (0x14).
     *
     * Les caracteres non-ASCII (e, e, c, a) retournent null car le HID basique
     * ne les supporte pas directement.
     */
    private fun charToHidAzerty(char: Char): Pair<Byte, Byte>? {
        // Lettres minuscules — positions physiques AZERTY
        val lowerLetterMap: Map<Char, Byte> = mapOf(
            'a' to 0x14.toByte(), // position du Q physique
            'z' to 0x1A.toByte(), // position du W physique
            'e' to 0x08.toByte(),
            'r' to 0x15.toByte(),
            't' to 0x17.toByte(),
            'y' to 0x1C.toByte(),
            'u' to 0x18.toByte(),
            'i' to 0x0C.toByte(),
            'o' to 0x12.toByte(),
            'p' to 0x13.toByte(),
            'q' to 0x04.toByte(), // position du A physique
            's' to 0x16.toByte(),
            'd' to 0x07.toByte(),
            'f' to 0x09.toByte(),
            'g' to 0x0A.toByte(),
            'h' to 0x0B.toByte(),
            'j' to 0x0D.toByte(),
            'k' to 0x0E.toByte(),
            'l' to 0x0F.toByte(),
            'm' to 0x33.toByte(), // position du ; physique
            'w' to 0x1D.toByte(), // position du Z physique
            'x' to 0x1B.toByte(),
            'c' to 0x06.toByte(),
            'v' to 0x19.toByte(),
            'b' to 0x05.toByte(),
            'n' to 0x11.toByte(),
        )

        // Lettres minuscules
        lowerLetterMap[char]?.let { code ->
            return Pair(0x00.toByte(), code)
        }

        // Lettres majuscules
        if (char in 'A'..'Z') {
            lowerLetterMap[char.lowercaseChar()]?.let { code ->
                return Pair(0x02.toByte(), code) // SHIFT
            }
        }

        // Chiffres — en AZERTY les chiffres sont en Shift sur la rangee du haut
        if (char in '1'..'9') {
            val code = (0x1E + (char - '1')).toByte()
            return Pair(0x02.toByte(), code) // SHIFT
        }
        if (char == '0') {
            return Pair(0x02.toByte(), 0x27.toByte()) // SHIFT
        }

        // Symboles — positions sans Shift sur la rangee des chiffres en AZERTY
        // Position physique 1 (0x1E) = & sans shift
        // Position physique 3 (0x20) = " sans shift
        // Position physique 4 (0x21) = ' sans shift
        // Position physique 5 (0x22) = ( sans shift
        // Position physique 6 (0x23) = - sans shift (section sign)
        // Position physique 8 (0x24) = _ sans shift
        // Les positions 2 (e), 7 (e), 9 (c), 0 (a) produisent des
        // caracteres non-ASCII, on ne les gere pas.

        return when (char) {
            '\n' -> Pair(0x00.toByte(), 0x28.toByte())
            '\t' -> Pair(0x00.toByte(), 0x2B.toByte())
            ' '  -> Pair(0x00.toByte(), 0x2C.toByte())

            // Symboles sans shift sur la rangee des chiffres AZERTY
            '&'  -> Pair(0x00.toByte(), 0x1E.toByte()) // touche 1
            '"'  -> Pair(0x00.toByte(), 0x20.toByte()) // touche 3
            '\'' -> Pair(0x00.toByte(), 0x21.toByte()) // touche 4
            '('  -> Pair(0x00.toByte(), 0x22.toByte()) // touche 5
            '-'  -> Pair(0x00.toByte(), 0x23.toByte()) // touche 6
            '_'  -> Pair(0x00.toByte(), 0x24.toByte()) // touche 8
            ')'  -> Pair(0x00.toByte(), 0x2D.toByte()) // touche )  (position du - en QWERTY)
            '='  -> Pair(0x00.toByte(), 0x2E.toByte()) // touche =  (position du = en QWERTY)

            // Ponctuation specifique AZERTY
            ','  -> Pair(0x00.toByte(), 0x10.toByte()) // touche M position QWERTY
            ';'  -> Pair(0x00.toByte(), 0x36.toByte()) // touche , position QWERTY
            ':'  -> Pair(0x00.toByte(), 0x37.toByte()) // touche . position QWERTY
            '!'  -> Pair(0x00.toByte(), 0x38.toByte()) // touche / position QWERTY
            '.'  -> Pair(0x02.toByte(), 0x36.toByte()) // Shift + , position QWERTY
            '/'  -> Pair(0x02.toByte(), 0x37.toByte()) // Shift + . position QWERTY
            '?'  -> Pair(0x02.toByte(), 0x10.toByte()) // Shift + M position QWERTY

            // Symboles Shift sur la rangee des chiffres AZERTY
            // 1+shift=1, 2+shift=2, etc. — deja gere au-dessus
            // Symboles supplementaires
            '*'  -> Pair(0x00.toByte(), 0x31.toByte()) // touche \ position QWERTY
            '+'  -> Pair(0x02.toByte(), 0x2E.toByte()) // Shift + =
            '['  -> Pair(0x02.toByte(), 0x2F.toByte()) // AltGr serait plus correct mais HID basique
            ']'  -> Pair(0x02.toByte(), 0x30.toByte())
            '{'  -> Pair(0x40.toByte(), 0x21.toByte()) // AltGr + 4
            '}'  -> Pair(0x40.toByte(), 0x2E.toByte()) // AltGr + =
            '\\' -> Pair(0x40.toByte(), 0x24.toByte()) // AltGr + 8
            '|'  -> Pair(0x40.toByte(), 0x23.toByte()) // AltGr + 6
            '@'  -> Pair(0x40.toByte(), 0x27.toByte()) // AltGr + 0
            '#'  -> Pair(0x40.toByte(), 0x20.toByte()) // AltGr + 3
            '~'  -> Pair(0x40.toByte(), 0x1F.toByte()) // AltGr + 2
            '`'  -> Pair(0x40.toByte(), 0x24.toByte()) // AltGr + 7 (approximation)
            '<'  -> Pair(0x00.toByte(), 0x64.toByte()) // touche < (102nd key)
            '>'  -> Pair(0x02.toByte(), 0x64.toByte()) // Shift + <
            '$'  -> Pair(0x00.toByte(), 0x30.toByte()) // touche ] position QWERTY
            '^'  -> Pair(0x00.toByte(), 0x2F.toByte()) // touche [ position QWERTY
            '%'  -> Pair(0x02.toByte(), 0x34.toByte()) // Shift + u-grave (approximation)

            else -> null // Caracteres non-ASCII (e, e, c, a, etc.)
        }
    }
}
