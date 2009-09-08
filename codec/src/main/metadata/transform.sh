mkdir html java

java Transform Nikon3MakerNote.xml xslt/MakerNoteToText.xslt > html/Nikon3MakerNote.html
java Transform Nikon3MakerNote.xml xslt/MakerNoteToTable.xslt >> html/Nikon3MakerNote.html

java Transform CanonCRWMakerNote.xml xslt/MakerNoteToText.xslt > html/CanonCRWMakerNote.html
java Transform CanonCRWMakerNote.xml xslt/MakerNoteToTable.xslt >> html/CanonCRWMakerNote.html

java Transform CanonCR2MakerNote.xml xslt/MakerNoteToText.xslt > html/CanonCR2MakerNote.html
java Transform CanonCR2MakerNote.xml xslt/MakerNoteToTable.xslt >> html/CanonCR2MakerNote.html

java Transform SonyMakerNote.xml xslt/MakerNoteToText.xslt > html/SonyMakerNote.html
java Transform SonyMakerNote.xml xslt/MakerNoteToTable.xslt >> html/SonyMakerNote.html

java Transform PentaxMakerNote.xml xslt/MakerNoteToText.xslt > html/PentaxMakerNote.html
java Transform PentaxMakerNote.xml xslt/MakerNoteToTable.xslt >> html/PentaxMakerNote.html

java Transform MinoltaMakerNote.xml xslt/MakerNoteToText.xslt > html/MinoltaMakerNote.html
java Transform MinoltaMakerNote.xml xslt/MakerNoteToTable.xslt >> html/MinoltaMakerNote.html

java Transform OlympusMakerNote.xml xslt/MakerNoteToText.xslt > html/OlympusMakerNote.html
java Transform OlympusMakerNote.xml xslt/MakerNoteToTable.xslt >> html/OlympusMakerNote.html

java Transform KodakMakerNote.xml xslt/MakerNoteToText.xslt > html/KodakMakerNote.html
java Transform KodakMakerNote.xml xslt/MakerNoteToTable.xslt >> html/KodakMakerNote.html
