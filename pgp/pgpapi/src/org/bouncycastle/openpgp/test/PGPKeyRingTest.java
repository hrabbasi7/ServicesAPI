package org.bouncycastle.openpgp.test;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Iterator;

import org.bouncycastle.jce.spec.ElGamalParameterSpec;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.test.SimpleTestResult;
import org.bouncycastle.util.test.Test;
import org.bouncycastle.util.test.TestResult;

public class PGPKeyRingTest
    implements Test
{
    byte[] pub1 = Base64.decode(
        "mQGiBEA83v0RBADzKVLVCnpWQxX0LCsevw/3OLs0H7MOcLBQ4wMO9sYmzGYn"
      + "xpVj+4e4PiCP7QBayWyy4lugL6Lnw7tESvq3A4v3fefcxaCTkJrryiKn4+Cg"
      + "y5rIBbrSKNtCEhVi7xjtdnDjP5kFKgHYjVOeIKn4Cz/yzPG3qz75kDknldLf"
      + "yHxp2wCgwW1vAE5EnZU4/UmY7l8kTNkMltMEAJP4/uY4zcRwLI9Q2raPqAOJ"
      + "TYLd7h+3k/BxI0gIw96niQ3KmUZDlobbWBI+VHM6H99vcttKU3BgevNf8M9G"
      + "x/AbtW3SS4De64wNSU3189XDG8vXf0vuyW/K6Pcrb8exJWY0E1zZQ1WXT0gZ"
      + "W0kH3g5ro//Tusuil9q2lVLF2ovJA/0W+57bPzi318dWeNs0tTq6Njbc/GTG"
      + "FUAVJ8Ss5v2u6h7gyJ1DB334ExF/UdqZGldp0ugkEXaSwBa2R7d3HBgaYcoP"
      + "Ck1TrovZzEY8gm7JNVy7GW6mdOZuDOHTxyADEEP2JPxh6eRcZbzhGuJuYIif"
      + "IIeLOTI5Dc4XKeV32a+bWrQidGVzdCAoVGVzdCBrZXkpIDx0ZXN0QHViaWNh"
      + "bGwuY29tPohkBBMRAgAkBQJAPN79AhsDBQkB4TOABgsJCAcDAgMVAgMDFgIB"
      + "Ah4BAheAAAoJEJh8Njfhe8KmGDcAoJWr8xgPr75y/Cp1kKn12oCCOb8zAJ4p"
      + "xSvk4K6tB2jYbdeSrmoWBZLdMLACAAC5AQ0EQDzfARAEAJeUAPvUzJJbKcc5"
      + "5Iyb13+Gfb8xBWE3HinQzhGr1v6A1aIZbRj47UPAD/tQxwz8VAwJySx82ggN"
      + "LxCk4jW9YtTL3uZqfczsJngV25GoIN10f4/j2BVqZAaX3q79a3eMiql1T0oE"
      + "AGmD7tO1LkTvWfm3VvA0+t8/6ZeRLEiIqAOHAAQNBACD0mVMlAUgd7REYy/1"
      + "mL99Zlu9XU0uKyUex99sJNrcx1aj8rIiZtWaHz6CN1XptdwpDeSYEOFZ0PSu"
      + "qH9ByM3OfjU/ya0//xdvhwYXupn6P1Kep85efMBA9jUv/DeBOzRWMFG6sC6y"
      + "k8NGG7Swea7EHKeQI40G3jgO/+xANtMyTIhPBBgRAgAPBQJAPN8BAhsMBQkB"
      + "4TOAAAoJEJh8Njfhe8KmG7kAn00mTPGJCWqmskmzgdzeky5fWd7rAKCNCp3u"
      + "ZJhfg0htdgAfIy8ppm05vLACAAA=");

    byte[] sec1 = Base64.decode(
        "lQHhBEA83v0RBADzKVLVCnpWQxX0LCsevw/3OLs0H7MOcLBQ4wMO9sYmzGYn"
      + "xpVj+4e4PiCP7QBayWyy4lugL6Lnw7tESvq3A4v3fefcxaCTkJrryiKn4+Cg"
      + "y5rIBbrSKNtCEhVi7xjtdnDjP5kFKgHYjVOeIKn4Cz/yzPG3qz75kDknldLf"
      + "yHxp2wCgwW1vAE5EnZU4/UmY7l8kTNkMltMEAJP4/uY4zcRwLI9Q2raPqAOJ"
      + "TYLd7h+3k/BxI0gIw96niQ3KmUZDlobbWBI+VHM6H99vcttKU3BgevNf8M9G"
      + "x/AbtW3SS4De64wNSU3189XDG8vXf0vuyW/K6Pcrb8exJWY0E1zZQ1WXT0gZ"
      + "W0kH3g5ro//Tusuil9q2lVLF2ovJA/0W+57bPzi318dWeNs0tTq6Njbc/GTG"
      + "FUAVJ8Ss5v2u6h7gyJ1DB334ExF/UdqZGldp0ugkEXaSwBa2R7d3HBgaYcoP"
      + "Ck1TrovZzEY8gm7JNVy7GW6mdOZuDOHTxyADEEP2JPxh6eRcZbzhGuJuYIif"
      + "IIeLOTI5Dc4XKeV32a+bWv4CAwJ5KgazImo+sGBfMhDiBcBTqyDGhKHNgHic"
      + "0Pky9FeRvfXTc2AO+jGmFPjcs8BnTWuDD0/jkQnRZpp1TrQidGVzdCAoVGVz"
      + "dCBrZXkpIDx0ZXN0QHViaWNhbGwuY29tPohkBBMRAgAkBQJAPN79AhsDBQkB"
      + "4TOABgsJCAcDAgMVAgMDFgIBAh4BAheAAAoJEJh8Njfhe8KmGDcAn3XeXDMg"
      + "BZgrZzFWU2IKtA/5LG2TAJ0Vf/jjyq0jZNZfGfoqGTvD2MAl0rACAACdAVgE"
      + "QDzfARAEAJeUAPvUzJJbKcc55Iyb13+Gfb8xBWE3HinQzhGr1v6A1aIZbRj4"
      + "7UPAD/tQxwz8VAwJySx82ggNLxCk4jW9YtTL3uZqfczsJngV25GoIN10f4/j"
      + "2BVqZAaX3q79a3eMiql1T0oEAGmD7tO1LkTvWfm3VvA0+t8/6ZeRLEiIqAOH"
      + "AAQNBACD0mVMlAUgd7REYy/1mL99Zlu9XU0uKyUex99sJNrcx1aj8rIiZtWa"
      + "Hz6CN1XptdwpDeSYEOFZ0PSuqH9ByM3OfjU/ya0//xdvhwYXupn6P1Kep85e"
      + "fMBA9jUv/DeBOzRWMFG6sC6yk8NGG7Swea7EHKeQI40G3jgO/+xANtMyTP4C"
      + "AwJ5KgazImo+sGBl2C7CFuI+5KM4ZhbtVie7l+OiTpr5JW2z5VgnV3EX9p04"
      + "LcGKfQvD65+ELwli6yh8B2zGcipqTaYk3QoYNIhPBBgRAgAPBQJAPN8BAhsM"
      + "BQkB4TOAAAoJEJh8Njfhe8KmG7kAniuRkaFFv1pdCBN8JJXpcorHmyouAJ9L"
      + "xxmusffR6OI7WgD3XZ0AL8zUC7ACAAA=");

    char[]    pass1 = "qwertzuiop".toCharArray();

    byte[] pub2 = Base64.decode(
         "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");

    byte[] sec2 = Base64.decode(
        "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"
      + "Nifs+ljmsAFn");


    char[]  sec2pass1 = "sandhya".toCharArray();
    char[]    sec2pass2 = "psai".toCharArray();

    byte[] pub3 = Base64.decode(
        "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"
      + "n3FaE6UZE9GMDmCIAjhI+0X9aH6CsAIAAw==");

    byte[] sec3 = Base64.decode(
        "lQHhBEB9BH0RBACtYQtE7tna6hgGyGLpq+ds3r2cLC0ISn5dNw7tm9vwiNVF"
      + "JA2N37RRrifw4PvgelRSvLaX3M3ZBqC9s1Metg3v4FSlIRtSLWCNpHSvNw7i"
      + "X8C2Xy9Hdlbh6Y/50o+iscojLRE14upfR1bIkcCZQGSyvGV52V2wBImUUZjV"
      + "s2ZngwCg7mu852vK7+euz4WaL7ERVYtq9CMEAJ5swrljerDpz/RQ4Lhp6KER"
      + "KyuI0PUttO57xINGshEINgYlZdGaZHRueHe7uKfI19mb0T4N3NJWaZ0wF+Cn"
      + "rixsq0VrTUfiwfZeGluNG73aTCeY45fVXMGTTSYXzS8T0LW100Xn/0g9HRyA"
      + "xUpuWo8IazxkMqHJis2uwriYKpAfA/9anvj5BS9p5pfPjp9dGM7GTMIYl5f2"
      + "fcP57f+AW1TVR6IZiMJAvAdeWuLtwLnJiFpGlnFz273pfl+sAuqm1yNceImR"
      + "2SDDP4+vtyycWy8nZhgEuhZx3W3cWMQz5WyNJSY1JJHh9TCQkCoN8E7XpVP4"
      + "zEPboB2GzD93mfD8JLHP+/4DAwIvYrn+YqRaaGAu19XUj895g/GROyP8WEaU"
      + "Bd/JNqWc4kE/0guetGnPzq7G3bLVwiKfFd4X7BrgHAo3mrQtVGVzdCBLZXkg"
      + "KG5vIGNvbW1lbnQpIDx0ZXN0QGJvdW5jeWNhc3RsZS5vcmc+iFkEExECABkF"
      + "AkB9BH0ECwcDAgMVAgMDFgIBAh4BAheAAAoJEKnMV8vjZQOpSRQAoKZy6YS1"
      + "irF5/Q3JlWiwbkN6dEuLAJ9lldRLOlXsuQ5JW1+SLEc6K9ho4rACAADRwXPB"
      + "cQEQAAEBAAAAAAAAAAAAAAAA/9j/4AAQSkZJRgABAQEASABIAAD//gAXQ3Jl"
      + "YXRlZCB3aXRoIFRoZSBHSU1Q/9sAQwAIBgYHBgUIBwcHCQkICgwUDQwLCwwZ"
      + "EhMPFB0aHx4dGhwcICQuJyAiLCMcHCg3KSwwMTQ0NB8nOT04MjwuMzQy/9sA"
      + "QwEJCQkMCwwYDQ0YMiEcITIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIy"
      + "MjIyMjIyMjIyMjIyMjIyMjIyMjIy/8AAEQgAFAAUAwEiAAIRAQMRAf/EABoA"
      + "AQACAwEAAAAAAAAAAAAAAAAEBQIDBgf/xAAoEAABAgUDBAEFAAAAAAAAAAAB"
      + "AgMABBEhMQUSQQYTIiNhFFGBkcH/xAAXAQEAAwAAAAAAAAAAAAAAAAAEAgMF"
      + "/8QAJBEAAQQAAwkAAAAAAAAAAAAAAQACERIEIfATMTJBUZGx0fH/2gAMAwEA"
      + "AhEDEQA/APMuotJlJVxstqaPo22NlAUp+YsNO0qSUtBcMu6n6EtOHcfPAHHF"
      + "I16++oajQtTA3DapK02HFR8UpE9pTbQWtKm2WG2rlxVyQTcfGbn7Qm0OIjL7"
      + "7Wrs2NNm9lzTmmSxQ0PX4opSprk5tmESF6syggzGwOLG6gXgHFbZhBixk8Xl"
      + "IDcOQLRKt+rX+3qC5ZLTQblpQlvwvxn9CMpZturVGkJHapQJphRH8hCLXbzr"
      + "qpYsCx1zC5rtpJNuYQhASc0UAQv/2YhcBBMRAgAcBQJAfQV+AhsDBAsHAwID"
      + "FQIDAxYCAQIeAQIXgAAKCRCpzFfL42UDqfa2AJ9hjtEeDTbTEAuuSbzhYFxN"
      + "/qc0FACgsmzysdbBpuN65yK01tbEaeIMtqCwAgAAnQEUBEB9BH4QAwCqRuWO"
      + "rxm5v/8WWBtPEUZnYuu3WYxefRKn6ON5ka5Ymp4AfKpKqKqn982tjlG13GVE"
      + "P2ow11L4yQKGNm+VyVEapm7SpMD0SHg9vsJygAWrfMJnBYWSgE19W6o3ckUR"
      + "sWsAAwYDAKDCSiSxC3Z5/POaCehw5iHs/vmVk5Wn831LSZEEwkSohNDbIEry"
      + "ia/9tFXSt11AP4stJtZ0MH87/6JBbiIRD4JXcx+zfzchL7Teczr/06QMphSn"
      + "sAENupbhLupfpR+BbP4DAwIvYrn+YqRaaGBjvFK1fbxCt7ZM4I2W/3BC0lCX"
      + "m/NypKNspGflec8u96uUlA0fNCnxm6f9nbB0jpvoKi0g4iqAf+P2iEYEGBEC"
      + "AAYFAkB9BH4ACgkQqcxXy+NlA6mtMgCgvccZA/Sg7BXVpxli47SYhxSHoM4A"
      + "oNCOMplSnYTuh5ikKeBWtz36gC1psAIAAA==");

    char[]  sec3pass1 = "123456".toCharArray();
    
    //
    // GPG comment packets.
    //
    byte[] sec4 = Base64.decode(
           "lQG7BD0PbK8RBAC0cW4Y2MZXmAmqYp5Txyw0kSQsFvwZKHNMFRv996IsN57URVF5"
        + "BGMVPRBi9dNucWbjiSYpiYN13wE9IuLZsvVaQojV4XWGRDc+Rxz9ElsXnsYQ3mZU"
        + "7H1bNQEofstChk4z+dlvPBN4GFahrIzn/CeVUn6Ut7dVdYbiTqviANqNXwCglfVA"
        + "2OEePvqFnGxs1jhJyPSOnTED/RwRvsLH/k43mk6UEvOyN1RIpBXN+Ieqs7h1gFrQ"
        + "kB+WMgeP5ZUsotTffVDSUS9UMxRQggVUW1Xml0geGwQsNfkr/ztWMs/T4xp1v5j+"
        + "QyJx6OqNlkGdqOsoqkzJx0SQ1zBxdinFyyC4H95SDAb/RQOu5LQmxFG7quexztMs"
        + "infEA/9cVc9+qCo92yRAaXRqKNVVQIQuPxeUsGMyVeJQvJBD4An8KTMCdjpF10Cp"
        + "qA3t+n1S0zKr5WRUtvS6y60MOONO+EJWVWBNkx8HJDaIMNkfoqQoz3Krn7w6FE/v"
        + "/5uwMd6jY3N3yJZn5nDZT9Yzv9Nx3j+BrY+henRlSU0c6xDc9QAAnjJYg0Z83VJG"
        + "6HrBcgc4+4K6lHulCqH9JiM6RFNBX2ZhY3RvcjoAAK9hV206agp99GI6x5qE9+pU"
        + "vs6O+Ich/SYjOkRTQV9mYWN0b3I6AACvYAfGn2FGrpBYbjnpTuFOHJMS/T5xg/0m"
        + "IzpEU0FfZmFjdG9yOgAAr0dAQz6XxMwxWIn8xIZR/v2iN2L9C6O0EkZvbyBCYXIg"
        + "PGJhekBxdXV4PohXBBMRAgAXBQI9D2yvBQsHCgMEAxUDAgMWAgECF4AACgkQUGLI"
        + "YCIktfoGogCfZiXMJUKrScqozv5tMwzTTk2AaT8AniM5iRr0Du/Y08SL/NMhtF6H"
        + "hJ89nO4EPQ9ssRADAI6Ggxj6ZBfoavuXd/ye99osW8HsNlbqhXObu5mCMNySX2wa"
        + "HoWyRUEaUkI9eQw+MlHzIwzA32E7y2mU3OQBKdgLcBg4jxtcWVEg8ESKF9MpFXxl"
        + "pExxWrr4DFBfCRcsTwAFEQL9G3OvwJuEZXgx2JSS41D3pG4/qiHYICVa0u3p/14i"
        + "cq0kXajIk5ZJ6frCIAHIzuQ3n7jjzr05yR8s/qCrNbBA+nlkVNa/samk+jCzxxxa"
        + "cR/Dbh2wkvTFuDFFETwQYLuZAADcDck4YGQAmHivVT2NNDCf/aTz0+CJWl+xRc2l"
        + "Qw7D/SQjOkVMR19mYWN0b3I6AACbBnv9m5/bb/pjYAm2PtDp0CysQ9X9JCM6RUxH"
        + "X2ZhY3RvcjoAAJsFyHnSmaWguTFf6lJ/j39LtUNtmf0kIzpFTEdfZmFjdG9yOgAA"
        + "mwfwMD3LxmWtuCWBE9BptWMNH07Z/SQjOkVMR19mYWN0b3I6AACbBdhBrbSiM4UN"
        + "y7khDW2Sk0e4v9mIRgQYEQIABgUCPQ9ssQAKCRBQYshgIiS1+jCMAJ9txwHnb1Kl"
        + "6i/fSoDs8SkdM7w48wCdFvPEV0sSxE73073YhBgPZtMWbBo=");

    //
    // PGP freeware version 7
    //
    byte[] pub5 = Base64.decode(
        "mQENBEBrBE4BCACjXVcNIFDQSofaIyZnALb2CRg+WY9uUqgHEEAOlPe03Cs5STM5"
      + "HDlNmrh4TdFceJ46rxk1mQOjULES1YfHay8lCIzrD7FX4oj0r4DC14Fs1vXaSar2"
      + "1szIpttOw3obL4A1e0p6N4jjsoG7N/pA0fEL0lSw92SoBrMbAheXRg4qNTZvdjOR"
      + "grcuOuwgJRvPLtRXlhyLBoyhkd5mmrIDGv8QHJ/UjpeIcRXY9kn9oGXnEYcRbMaU"
      + "VwXB4pLzWqz3ZejFI3lOxRWjm760puPOnGYlzSVBxlt2LgzUgSj1Mn+lIpWmAzsa"
      + "xEiU4xUwEomQns72yYRZ6D3euNCibcte4SeXABEBAAG0KXBhbGFzaCBrYXNvZGhh"
      + "biA8cGthc29kaGFuQHRpYWEtY3JlZi5vcmc+iQEuBBABAgAYBQJAawROCAsBAwkI"
      + "BwIKAhkBBRsDAAAAAAoJEOfelumuiOrYqPEH+wYrdP5Tq5j+E5yN1pyCg1rwbSOt"
      + "Dka0y0p7Oq/VIGLk692IWPItLEunnBXQtGBcWqklrvogvlhxtf16FgoyScfLJx1e"
      + "1cJa+QQnVuH+VOESN6iS9Gp9lUfVOHv74mEMXw0l2Djfy/lnrkAMBatggyGnF9xF"
      + "VXOLk1J2WVFm9KUE23o6qdB7RGkf31pN2eA7SWmkdJSkUH7o/QSFBI+UTRZ/IY5P"
      + "ZIJpsdiIOqd9YMG/4RoSZuPqNRR6x7BSs8nQVR9bYs4PPlp4GfdRnOcRonoTeJCZ"
      + "83RnsraWJnJTg34gRLBcqumhTuFKc8nuCNK98D6zkQESdcHLLTquCOaF5L+5AQ0E"
      + "QGsETwEIAOVwNCTaDZvW4dowPbET1bI5UeYY8rAGLYsWSUfgaFv2srMiApyBVltf"
      + "i6OLcPjcUCHDBjCv4pwx/C4qcHWb8av4xQIpqQXOpO9NxYE1eZnel/QB7DtH12ZO"
      + "nrDNmHtaXlulcKNGe1i1utlFhgzfFx6rWkRL0ENmkTkaQmPY4gTGymJTUhBbsSRq"
      + "2ivWqQA1TPwBuda73UgslIAHRd/SUaxjXoLpMbGOTeqzcKGjr5XMPTs7/YgBpWPP"
      + "UxMlEQIiU3ia1bxpEhx05k97ceK6TSH2oCPQA7gumjxOSjKT+jEm+8jACVzymEmc"
      + "XRy4D5Ztqkw/Z16pvNcu1DI5m6xHwr8AEQEAAYkBIgQYAQIADAUCQGsETwUbDAAA"
      + "AAAKCRDn3pbprojq2EynB/4/cEOtKbI5UisUd3vkTzvWOcqWUqGqi5wjjioNtIM5"
      + "pur2nFvhQE7SZ+PbAa87HRJU/4WcWMcoLkHD48JrQwHCHOLHSV5muYowb78X4Yh9"
      + "epYtSJ0uUahcn4Gp48p4BkhgsPYXkxEImSYzAOWStv21/7WEMqItMYl89BV6Upm8"
      + "HyTJx5MPTDbMR7X51hRg3OeQs6po3WTCWRzFIMyGm1rd/VK1L5ZDFPqO3S6YUJ0z"
      + "cxecYruvfK0Wp7q834wE8Zkl/PQ3NhfEPL1ZiLr/L00Ty+77/FZqt8SHRCICzOfP"
      + "OawcVGI+xHVXW6lijMpB5VaVIH8i2KdBMHXHtduIkPr9");
      
    byte[] sec5 = Base64.decode(
        "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");

    char[]  sec5pass1 = "12345678".toCharArray();

        //
        // Werner Koch "odd keys"
        //
    byte[] pub6 = Base64.decode(
        "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"
      + "L4Kb7TWkd/OHQScVBO8sTUz0+2g=");

    byte[] pub6check = Base64.decode("=62O9");

    //
    // revoked sub key
    //
    byte[] pub7 = Base64.decode(
        "mQGiBEFOsIwRBADcjRx7nAs4RaWsQU6p8/ECLZD9sSeYc6CN6UDI96RKj0/hCzMs"
      + "qlA0+9fzGZ7ZEJ34nuvDKlhKGC7co5eOiE0a9EijxgcrZU/LClZWa4YfyNg/ri6I"
      + "yTyfOfrPQ33GNQt2iImDf3FKp7XKuY9nIxicGQEaW0kkuAmbV3oh0+9q8QCg/+fS"
      + "epDEqEE/+nKONULGizKUjMED/RtL6RThRftZ9DOSdBytGYd48z35pca/qZ6HA36K"
      + "PVQwi7V77VKQyKFLTOXPLnVyO85hyYB/Nv4DFHN+vcC7/49lfoyYMZlN+LarckHi"
      + "NL154wmmzygB/KKysvWBLgkErEBCD0xBDd89iTQNlDtVQAWGORVffl6WWjOAkliG"
      + "3dL6A/9A288HfFRnywqi3xddriV6wCPmStC3dkCS4vHk2ofS8uw4ZNoRlp1iEPna"
      + "ai2Xa9DX1tkhaGk2k96MqqbBdGpbW8sMA9otJ9xdMjWEm/CgJUFUFQf3zaVy3mkM"
      + "S2Lvb6P4Wc2l/diEEIyK8+PqJItSh0OVU3K9oM7ngHwVcalKILQVUkV2b2tlZCA8"
      + "UmV2b2tlZEB0ZWQ+iQBOBBARAgAOBQJBTrCMBAsDAgECGQEACgkQvglkcFA/c63+"
      + "QgCguh8rsJbPTtbhZcrqBi5Mo1bntLEAoPZQ0Kjmu2knRUpHBeUemHDB6zQeuQIN"
      + "BEFOsIwQCAD2Qle3CH8IF3KiutapQvMF6PlTETlPtvFuuUs4INoBp1ajFOmPQFXz"
      + "0AfGy0OplK33TGSGSfgMg71l6RfUodNQ+PVZX9x2Uk89PY3bzpnhV5JZzf24rnRP"
      + "xfx2vIPFRzBhznzJZv8V+bv9kV7HAarTW56NoKVyOtQa8L9GAFgr5fSI/VhOSdvN"
      + "ILSd5JEHNmszbDgNRR0PfIizHHxbLY7288kjwEPwpVsYjY67VYy4XTjTNP18F1dD"
      + "ox0YbN4zISy1Kv884bEpQBgRjXyEpwpy1obEAxnIByl6ypUM2Zafq9AKUJsCRtMI"
      + "PWakXUGfnHy9iUsiGSa6q6Jew1XpMgs7AAICB/93zriSvSHqsi1FeEmUBo431Jkh"
      + "VerIzb6Plb1j6FIq+s3vyvx9K+dMvjotZqylWZj4GXpH+2xLJTjWkrGSfUZVI2Nk"
      + "nyOFxUCKLLqaqVBFAQIjULfvQfGEWiGQKk9aRLkdG+D+8Y2N9zYoBXoQ9arvvS/t"
      + "4mlOsiuaTe+BZ4x+BXTpF4b9sKZl7V8QP/TkoJWUdydkvxciHdWp7ssqyiKOFRhG"
      + "818knDfFQ3cn2w/RnOb+7AF9wDncXDPYLfpPv9b2qZoLrXcyvlLffGDUdWs553ut"
      + "1F5AprMURs8BGmY9BnjggfVubHdhTUoA4gVvrdaf+D9NwZAl0xK/5Y/oPuMZiQBG"
      + "BBgRAgAGBQJBTrCMAAoJEL4JZHBQP3Ot09gAoMmLKloVDP+WhDXnsM5VikxysZ4+"
      + "AKCrJAUO+lYAyPYwEwgK+bKmUGeKrIkARgQoEQIABgUCQU6wpQAKCRC+CWRwUD9z"
      + "rQK4AJ98kKFxGU6yhHPr6jYBJPWemTNOXgCfeGB3ox4PXeS4DJDuLy9yllytOjo=");

    byte[] pub7check = Base64.decode("=f/YQ");
    
    byte[] pub8 = Base64.decode(
              "mQGiBEEcraYRBADFYj+uFOhHz5SdECvJ3Z03P47gzmWLQ5HH8fPYC9rrv7AgqFFX"
            + "aWlJJVMLua9e6xoCiDWJs/n4BbZ/weL/11ELg6XqUnzFhYyz0H2KFsPgQ/b9lWLY"
            + "MtcPMFy5jE33hv/ixHgYLFqoNaAIbg0lzYEW/otQ9IhRl16fO1Q/CQZZrQCg/9M2"
            + "V2BTmm9RYog86CXJtjawRBcD/RIqU0zulxZ2Zt4javKVxrGIwW3iBU935ebmJEIK"
            + "Y5EVkGKBOCvsApZ+RGzpYeR2uMsTnQi8RJgiAnjaoVPCdsVJE7uQ0h8XuJ5n5mJ2"
            + "kLCFlF2hj5ViicZzse+crC12CGtgRe8z23ubLRcd6IUGhVutK8/b5knZ22vE14JD"
            + "ykKdA/96ObzJQdiuuPsEWN799nUUCaYWPAoLAmiXuICSP4GEnxLbYHWo8zhMrVMT"
            + "9Q5x3h8cszUz7Acu2BXjP1m96msUNoxPOZtt88NlaFz1Q/JSbQTsVOMd9b/IRN6S"
            + "A/uU0BiKEMHXuT8HUHVPK49oCKhZrGFP3RT8HZxDKLmR/qrgZ7ABh7QhSmlhIFlp"
            + "eXUgPHl5amlhQG5vd21lZGlhdGVjaC5jb20+sAMD//+JAF0EEBECAB0FAkEcraYH"
            + "CwkIBwMCCgIZAQUbAwAAAAUeAQAAAAAKCRD0/lb4K/9iFJlhAKCRMifQewiX5o8F"
            + "U099FG3QnLVUZgCfWpMOsHulGHfNrxdBSkE5Urqh1ymwAWe5Ag0EQRytphAIAPZC"
            + "V7cIfwgXcqK61qlC8wXo+VMROU+28W65Szgg2gGnVqMU6Y9AVfPQB8bLQ6mUrfdM"
            + "ZIZJ+AyDvWXpF9Sh01D49Vlf3HZSTz09jdvOmeFXklnN/biudE/F/Ha8g8VHMGHO"
            + "fMlm/xX5u/2RXscBqtNbno2gpXI61Brwv0YAWCvl9Ij9WE5J280gtJ3kkQc2azNs"
            + "OA1FHQ98iLMcfFstjvbzySPAQ/ClWxiNjrtVjLhdONM0/XwXV0OjHRhs3jMhLLUq"
            + "/zzhsSlAGBGNfISnCnLWhsQDGcgHKXrKlQzZlp+r0ApQmwJG0wg9ZqRdQZ+cfL2J"
            + "SyIZJrqrol7DVekyCzsAAgIH/3K2wKRSzkIpDfZR25+tnQ8brv3TYoDZo3/wN3F/"
            + "r6PGjx0150Q8g8EAC0bqm4rXWzOqdSxYxvIPOAGm5P4y+884yS6j3vKcXitT7vj+"
            + "ODc2pVwGDLDjrMRrosSK89ycPCK6R/5pD7Rv4l9DWi2fgLvXqJHS2/ujUf2uda9q"
            + "i9xNMnBXIietR82Sih4undFUOwh6Mws/o3eed9DIdaqv2Y2Aw43z/rJ6cjSGV3C7"
            + "Rkf9x85AajYA3LwpS8d99tgFig2u6V/A16oi6/M51oT0aR/ZAk50qUc4WBk9uRUX"
            + "L3Y+P6v6FCBE/06fgVltwcQHO1oKYKhH532tDL+9mW5/dYGwAYeJAEwEGBECAAwF"
            + "AkEcraYFGwwAAAAACgkQ9P5W+Cv/YhShrgCg+JW8m5nF3R/oZGuG87bXQBszkjMA"
            + "oLhGPncuGKowJXMRVc70/8qwXQJLsAFnmQGiBD2K5rYRBADD6kznWZA9nH/pMlk0"
            + "bsG4nI3ELgyI7KpgRSS+Dr17+CCNExxCetT+fRFpiEvUcSxeW4pOe55h0bQWSqLo"
            + "MNErXVJEXrm1VPkC08W8D/gZuPIsdtKJu4nowvdoA+WrI473pbeONGjaEDbuIJak"
            + "yeKM1VMSGhsImdKtxqhndq2/6QCg/xARUIzPRvKr2TJ52K393895X1kEAMCdjSs+"
            + "vABnhaeNNR5+NNkkIOCCjCS8qZRZ4ZnIayvn9ueG3KrhZeBIHoajUHrlTXBVj7XO"
            + "wXVfGpW17jCDiqhU8Pu6VwEwX1iFbuUwqBffiRLXKg0zfcN+MyFKToi+VsJi4jiZ"
            + "zcwUFMb8jE8tvR/muXti7zKPRPCbNBExoCt4A/0TgkzAosG/W4dUkkbc6XoHrjob"
            + "iYuy6Xbs/JYlV0vf2CyuKCZC6UoznO5x2GkvOyVtAgyG4HSh1WybdrutZ8k0ysks"
            + "mOthE7n7iczdj9Uwg2h+TfgDUnxcCAwxnOsX5UaBqGdkX1PjCWs+O3ZhUDg6UsZc"
            + "7O5a3kstf16lHpf4q7ABAIkAYQQfEQIAIQUCPYrmtgIHABcMgBHRi/xlIgI+Q6LT"
            + "kNJ7zKvTd87NHAAKCRDJM3gHb/sRj7bxAJ9f6mdlXQH7gMaYiY5tBe/FRtPr1gCf"
            + "UhDJQG0ARvORFWHjwhhBMLxW7j2wAWC0KkRlc21vbmQgS2VlIDxkZXNtb25kLmtl"
            + "ZUBub3dtZWRpYXRlY2guY29tPrADAQD9iQBYBBARAgAYBQI9iua2CAsDCQgHAgEK"
            + "AhkBBRsDAAAAAAoJEMkzeAdv+xGP7v4An19iqadBCCgDIe2DTpspOMidwQYPAJ4/"
            + "5QXbcn4ClhOKTO3ZEZefQvvL27ABYLkCDQQ9iua2EAgA9kJXtwh/CBdyorrWqULz"
            + "Bej5UxE5T7bxbrlLOCDaAadWoxTpj0BV89AHxstDqZSt90xkhkn4DIO9ZekX1KHT"
            + "UPj1WV/cdlJPPT2N286Z4VeSWc39uK50T8X8dryDxUcwYc58yWb/Ffm7/ZFexwGq"
            + "01uejaClcjrUGvC/RgBYK+X0iP1YTknbzSC0neSRBzZrM2w4DUUdD3yIsxx8Wy2O"
            + "9vPJI8BD8KVbGI2Ou1WMuF040zT9fBdXQ6MdGGzeMyEstSr/POGxKUAYEY18hKcK"
            + "ctaGxAMZyAcpesqVDNmWn6vQClCbAkbTCD1mpF1Bn5x8vYlLIhkmuquiXsNV6TIL"
            + "OwACAgf/SO+bbg+owbFKVN5HgOjOElQZVnCsegwCLqTeQzPPzsWmkGX2qZJPDIRN"
            + "RZfJzti6+oLJwaRA/3krjviUty4VKhZ3lKg8fd9U0jEdnw+ePA7yJ6gZmBHL15U5"
            + "OKH4Zo+OVgDhO0c+oetFpend+eKcvtoUcRoQoi8VqzYUNG0b/nmZGDlxQe1/ZNbP"
            + "HpNf1BAtJXivCEKMD6PVzsLPg2L4tFIvD9faeeuKYQ4jcWtTkBLuIaZba3i3a4wG"
            + "xTN20j9HpISVuLW/EfZAK1ef4DNjLmHEU9dMzDqfi+hPmMbGlFqcKr+VjcYIDuje"
            + "o+92xm/EWAmlti88r2hZ3MySamHDrLABAIkATAQYEQIADAUCPYrmtgUbDAAAAAAK"
            + "CRDJM3gHb/sRjzVTAKDVS+OJLMeS9VLAmT8atVCB42MwIQCgoh1j3ccWnhc/h6B7"
            + "9Uqz3fUvGoewAWA=");

    byte[] sec8 = Base64.decode(
              "lQHpBEEcraYRBADFYj+uFOhHz5SdECvJ3Z03P47gzmWLQ5HH8fPYC9rrv7AgqFFX"
            + "aWlJJVMLua9e6xoCiDWJs/n4BbZ/weL/11ELg6XqUnzFhYyz0H2KFsPgQ/b9lWLY"
            + "MtcPMFy5jE33hv/ixHgYLFqoNaAIbg0lzYEW/otQ9IhRl16fO1Q/CQZZrQCg/9M2"
            + "V2BTmm9RYog86CXJtjawRBcD/RIqU0zulxZ2Zt4javKVxrGIwW3iBU935ebmJEIK"
            + "Y5EVkGKBOCvsApZ+RGzpYeR2uMsTnQi8RJgiAnjaoVPCdsVJE7uQ0h8XuJ5n5mJ2"
            + "kLCFlF2hj5ViicZzse+crC12CGtgRe8z23ubLRcd6IUGhVutK8/b5knZ22vE14JD"
            + "ykKdA/96ObzJQdiuuPsEWN799nUUCaYWPAoLAmiXuICSP4GEnxLbYHWo8zhMrVMT"
            + "9Q5x3h8cszUz7Acu2BXjP1m96msUNoxPOZtt88NlaFz1Q/JSbQTsVOMd9b/IRN6S"
            + "A/uU0BiKEMHXuT8HUHVPK49oCKhZrGFP3RT8HZxDKLmR/qrgZ/4JAwLXyWhb4pf4"
            + "nmCmD0lDwoYvatLiR7UQVM2MamxClIiT0lCPN9C2AYIFgRWAJNS215Tjx7P/dh7e"
            + "8sYfh5XEHErT3dMbsAGHtCFKaWEgWWl5dSA8eXlqaWFAbm93bWVkaWF0ZWNoLmNv"
            + "bT6wAwP//4kAXQQQEQIAHQUCQRytpgcLCQgHAwIKAhkBBRsDAAAABR4BAAAAAAoJ"
            + "EPT+Vvgr/2IUmWEAoJEyJ9B7CJfmjwVTT30UbdCctVRmAJ9akw6we6UYd82vF0FK"
            + "QTlSuqHXKbABZ50CawRBHK2mEAgA9kJXtwh/CBdyorrWqULzBej5UxE5T7bxbrlL"
            + "OCDaAadWoxTpj0BV89AHxstDqZSt90xkhkn4DIO9ZekX1KHTUPj1WV/cdlJPPT2N"
            + "286Z4VeSWc39uK50T8X8dryDxUcwYc58yWb/Ffm7/ZFexwGq01uejaClcjrUGvC/"
            + "RgBYK+X0iP1YTknbzSC0neSRBzZrM2w4DUUdD3yIsxx8Wy2O9vPJI8BD8KVbGI2O"
            + "u1WMuF040zT9fBdXQ6MdGGzeMyEstSr/POGxKUAYEY18hKcKctaGxAMZyAcpesqV"
            + "DNmWn6vQClCbAkbTCD1mpF1Bn5x8vYlLIhkmuquiXsNV6TILOwACAgf/crbApFLO"
            + "QikN9lHbn62dDxuu/dNigNmjf/A3cX+vo8aPHTXnRDyDwQALRuqbitdbM6p1LFjG"
            + "8g84Aabk/jL7zzjJLqPe8pxeK1Pu+P44NzalXAYMsOOsxGuixIrz3Jw8IrpH/mkP"
            + "tG/iX0NaLZ+Au9eokdLb+6NR/a51r2qL3E0ycFciJ61HzZKKHi6d0VQ7CHozCz+j"
            + "d5530Mh1qq/ZjYDDjfP+snpyNIZXcLtGR/3HzkBqNgDcvClLx3322AWKDa7pX8DX"
            + "qiLr8znWhPRpH9kCTnSpRzhYGT25FRcvdj4/q/oUIET/Tp+BWW3BxAc7WgpgqEfn"
            + "fa0Mv72Zbn91gf4JAwITijME9IlFBGAwH6YmBtWIlnDiRbsq/Pxozuhbnes831il"
            + "KmdpUKXkiIfHY0MqrEWl3Dfn6PMJGTnhgqXMrDxx3uHrq0Jl2swRnAWIIO8gID7j"
            + "uPetUqEviPiwAYeJAEwEGBECAAwFAkEcraYFGwwAAAAACgkQ9P5W+Cv/YhShrgCg"
            + "+JW8m5nF3R/oZGuG87bXQBszkjMAoLhGPncuGKowJXMRVc70/8qwXQJLsAFn");
    
    char[]  sec8pass = "qwertyui".toCharArray();
    
    byte[] sec9 = Base64.decode(
              "lQGqBEHCokERBAC9rh5SzC1sX1y1zoFuBB/v0SGhoKMEvLYf8Qv/j4deAMrc"
            + "w5dxasYoD9oxivIUfTbZKo8cqr+dKLgu8tycigTM5b/T2ms69SUAxSBtj2uR"
            + "LZrh4vjC/93kF+vzYJ4fNaBs9DGfCnsTouKjXqmfN3SlPMKNcGutO7FaUC3d"
            + "zcpYfwCg7qyONHvXPhS0Iw4QL3mJ/6wMl0UD/0PaonqW0lfGeSjJSM9Jx5Bt"
            + "fTSlwl6GmvYmI8HKvOBXAUSTZSbEkMsMVcIgf577iupzgWCgNF6WsNqQpKaq"
            + "QIq1Kjdd0Y00xU1AKflOkhl6eufTigjviM+RdDlRYsOO5rzgwDTRTu9giErs"
            + "XIyJAIZIdu2iaBHX1zHTfJ1r7nlAA/9H4T8JIhppUk/fLGsoPNZzypzVip8O"
            + "mFb9PgvLn5GmuIC2maiocT7ibbPa7XuXTO6+k+323v7PoOUaKD3uD93zHViY"
            + "Ma4Q5pL5Ajc7isnLXJgJb/hvvB1oo+wSDo9vJX8OCSq1eUPUERs4jm90/oqy"
            + "3UG2QVqs5gcKKR4o48jTiv4DZQJHTlUBtB1mb28ga2V5IDxmb28ua2V5QGlu"
            + "dmFsaWQuY29tPoheBBMRAgAeBQJBwqJCAhsDBgsJCAcDAgMVAgMDFgIBAh4B"
            + "AheAAAoJEOKcXvehtw4ajJMAoK9nLfsrRY6peq56l/KzmjzuaLacAKCXnmiU"
            + "waI7+uITZ0dihJ3puJgUz50BWARBwqJDEAQA0DPcNIn1BQ4CDEzIiQkegNPY"
            + "mkYyYWDQjb6QFUXkuk1WEB73TzMoemsA0UKXwNuwrUgVhdpkB1+K0OR/e5ik"
            + "GhlFdrDCqyT+mw6dRWbJ2i4AmFXZaRKO8AozZeWojsfP1/AMxQoIiBEteMFv"
            + "iuXnZ3pGxSfZYm2+33IuPAV8KKMAAwUD/0C2xZQXgVWTiVz70HUviOmeTQ+f"
            + "b1Hj0U9NMXWB383oQRBZCvQDM12cqGsvPZuZZ0fkGehGAIoyXtIjJ9lejzZN"
            + "1TE9fnXZ9okXI4yCl7XLSE26OAbNsis4EtKTNScNaU9Dk3CS5XD/pkRjrkPN"
            + "2hdUFtshuGmYkqhb9BIlrwE7/gMDAglbVSwecr9mYJcDYCH62U9TScWDTzsQ"
            + "NFEfhMez3hGnNHNfHe+7yN3+Q9/LIhbba3IJEN5LsE5BFvudLbArp56EusIn"
            + "JCxgiEkEGBECAAkFAkHCokMCGwwACgkQ4pxe96G3Dho2UQCeN3VPwx3dROZ+"
            + "4Od8Qj+cLrBndGEAn0vaQdy6eIGeDw2I9u3Quwy6JnROnQHhBEHCozMRBADH"
            + "ZBlB6xsAnqFYtYQOHr4pX6Q8TrqXCiHHc/q56G2iGbI9IlbfykQzaPHgWqZw"
            + "9P0QGgF/QZh8TitiED+imLlGDqj3nhzpazqDh5S6sg6LYkQPqhwG/wT5sZQQ"
            + "fzdeupxupjI5YN8RdIqkWF+ILOjk0+awZ4z0TSY/f6OSWpOXlwCgjIquR3KR"
            + "tlCLk+fBlPnOXaOjX+kEAJw7umykNIHNaoY/2sxNhQhjqHVxKyN44y6FCSv9"
            + "jRyW8Q/Qc8YhqBIHdmlcXoNWkDtlvErjdYMvOKFqKB1e2bGpjvhtIhNVQWdk"
            + "oHap9ZuM1nV0+fD/7g/NM6D9rOOVCahBG2fEEeIwxa2CQ7zHZYfg9Umn3vbh"
            + "TYi68R3AmgLOA/wKIVkfFKioI7iX4crQviQHJK3/A90SkrjdMQwLoiUjdgtk"
            + "s7hJsTP1OPb2RggS1wCsh4sv9nOyDULj0T0ySGv7cpyv5Nq0FY8gw2oogHs5"
            + "fjUnG4VeYW0zcIzI8KCaJT4UhR9An0A1jF6COrYCcjuzkflFbQLtQb9uNj8a"
            + "hCpU4/4DAwIUxXlRMYE8uWCranzPo83FnBPRnGJ2aC9SqZWJYVUKIn4Vf2nu"
            + "pVvCGFja0usl1WfV72hqlNKEONq7lohJBBgRAgAJBQJBwqMzAhsCAAoJEOKc"
            + "Xvehtw4afisAoME/t8xz/rj/N7QRN9p8Ji8VPGSqAJ9K8eFJ+V0mxR+octJr"
            + "6neEEX/i1Q==");

    public char[] sec9pass = "foo".toCharArray();
    
    private boolean notEqual(
        byte[]    b1,
        byte[]    b2)
    {
        if (b1.length != b2.length)
        {
            return true;
        }
        
        for (int i = 0; i != b2.length; i++)
        {
            if (b1[i] != b2[i])
            {
                return true;
            }
        }
        
        return false;
    }
    
    public TestResult test1()
    {
        try
        {
            PGPPublicKeyRingCollection    pubRings = new PGPPublicKeyRingCollection(pub1);

            int                                        count = 0;

            Iterator    rIt = pubRings.getKeyRings();
            
            while (rIt.hasNext())
            {
                PGPPublicKeyRing                    pgpPub = (PGPPublicKeyRing)rIt.next();

                count++;
                
                int    keyCount = 0;
                
                byte[]    bytes = pgpPub.getEncoded();
                
                pgpPub = new PGPPublicKeyRing(bytes);
                
                Iterator    it = pgpPub.getPublicKeys();
                while (it.hasNext())
                {
                    keyCount++;

                    PGPPublicKey    pubKey = (PGPPublicKey)it.next();
                    
                    Iterator   sIt = pubKey.getSignatures();
                    while (sIt.hasNext())
                    {
                        ((PGPSignature)sIt.next()).getSignatureType();
                    }
                }
                
                if (keyCount != 2)
                {
                    return new SimpleTestResult(false, getName() + ": wrong number of public keys");
                }
            }
            
            if (count != 1)
            {
                return new SimpleTestResult(false, getName() + ": wrong number of public keyrings");
            }
            
            //
            // exact match
            //
            rIt = pubRings.getKeyRings("test (Test key) <test@ubicall.com>");
            count = 0;
            while (rIt.hasNext())
            {
                count++;
                rIt.next();
            }
            
            if (count != 1)
            {
                return new SimpleTestResult(false, getName() + ": wrong number of public keyrings on exact match");
            }
            
            //
            // partial match 1 expected
            //
            rIt = pubRings.getKeyRings("test", true);
            count = 0;
            while (rIt.hasNext())
            {
                count++;
                rIt.next();
            }
            
            if (count != 1)
            {
                return new SimpleTestResult(false, getName() + ": wrong number of public keyrings on partial match 1");
            }
            
            //
            // partial match 0 expected
            //
            rIt = pubRings.getKeyRings("XXX", true);
            count = 0;
            while (rIt.hasNext())
            {
                count++;
                rIt.next();
            }
            
            if (count != 0)
            {
                return new SimpleTestResult(false, getName() + ": wrong number of public keyrings on partial match 0");
            }
            
            PGPSecretKeyRingCollection    secretRings = new PGPSecretKeyRingCollection(sec1);

            rIt = secretRings.getKeyRings();
            count = 0;
            
            while (rIt.hasNext())
            {
                PGPSecretKeyRing                    pgpSec = (PGPSecretKeyRing)rIt.next();
        
                count++;
                
                int    keyCount = 0;
                
                byte[]    bytes = pgpSec.getEncoded();
                
                pgpSec = new PGPSecretKeyRing(bytes);
                
                Iterator    it = pgpSec.getSecretKeys();
                while (it.hasNext())
                {
                    keyCount++;

                    PGPSecretKey    k = (PGPSecretKey)it.next();
                    PGPPublicKey    pk = k.getPublicKey();
                    
                    pk.getSignatures();
                    
                    byte[] pkBytes = pk.getEncoded();
                    
                    PGPPublicKeyRing  pkR = new PGPPublicKeyRing(pkBytes);
                }
                
                if (keyCount != 2)
                {
                    return new SimpleTestResult(false, getName() + ": wrong number of secret keys");
                }
            }
            
            if (count != 1)
            {
                return new SimpleTestResult(false, getName() + ": wrong number of secret keyrings");
            }
            
            //
            // exact match
            //
            rIt = secretRings.getKeyRings("test (Test key) <test@ubicall.com>");
            count = 0;
            while (rIt.hasNext())
            {
                count++;
                rIt.next();
            }
            
            if (count != 1)
            {
                return new SimpleTestResult(false, getName() + ": wrong number of secret keyrings on exact match");
            }
            
            //
            // partial match 1 expected
            //
            rIt = secretRings.getKeyRings("test", true);
            count = 0;
            while (rIt.hasNext())
            {
                count++;
                rIt.next();
            }
            
            if (count != 1)
            {
                return new SimpleTestResult(false, getName() + ": wrong number of secret keyrings on partial match 1");
            }
            
            //
            // exact match 0 expected
            //
            rIt = secretRings.getKeyRings("test", false);
            count = 0;
            while (rIt.hasNext())
            {
                count++;
                rIt.next();
            }
            
            if (count != 0)
            {
                return new SimpleTestResult(false, getName() + ": wrong number of secret keyrings on partial match 0");
            }
            return new SimpleTestResult(true, getName() + ": Okay");
        }
        catch (Exception e)
        {
            if (e instanceof PGPException)
            {
                if (((PGPException)e).getUnderlyingException() != null)
                {
                    ((PGPException)e).getUnderlyingException().printStackTrace();
                }
            }
            else
            {
                e.printStackTrace();
            }
            return new SimpleTestResult(false, getName() + ": exception - " + e.toString());
        }
    }
    
    public TestResult test2()
    {
        try
        {
            PGPPublicKeyRingCollection    pubRings = new PGPPublicKeyRingCollection(pub2);

            int                            count = 0;

            byte[]    encRing = pubRings.getEncoded();

            pubRings = new PGPPublicKeyRingCollection(encRing);
            
            Iterator    rIt = pubRings.getKeyRings();
            
            while (rIt.hasNext())
            {
                PGPPublicKeyRing        pgpPub = (PGPPublicKeyRing)rIt.next();
                
                count++;
                
                int    keyCount = 0;
                
                byte[]    bytes = pgpPub.getEncoded();
                
                pgpPub = new PGPPublicKeyRing(bytes);
                
                Iterator    it = pgpPub.getPublicKeys();
                while (it.hasNext())
                {
                    PGPPublicKey    pk = (PGPPublicKey)it.next();
                    
                    byte[] pkBytes = pk.getEncoded();
                    
                    PGPPublicKeyRing  pkR = new PGPPublicKeyRing(pkBytes);
                    
                    keyCount++;
                }
                
                if (keyCount != 2)
                {
                    return new SimpleTestResult(false, getName() + ": wrong number of public keys");
                }
            }
            
            if (count != 2)
            {
                return new SimpleTestResult(false, getName() + ": wrong number of public keyrings");
            }
            
            PGPSecretKeyRingCollection    secretRings = new PGPSecretKeyRingCollection(sec2);

            rIt = secretRings.getKeyRings();
            count = 0;
            
            encRing = secretRings.getEncoded();
            
            secretRings = new PGPSecretKeyRingCollection(encRing);
            
            while (rIt.hasNext())
            {
                PGPSecretKeyRing                    pgpSec = (PGPSecretKeyRing)rIt.next();
        
                count++;
                
                int    keyCount = 0;
                
                byte[]    bytes = pgpSec.getEncoded();
                
                pgpSec = new PGPSecretKeyRing(bytes);
                
                Iterator    it = pgpSec.getSecretKeys();
                while (it.hasNext())
                {
                    keyCount++;

                    PGPSecretKey    k = (PGPSecretKey)it.next();
                    PGPPublicKey    pk = k.getPublicKey();

                    if (pk.getKeyID() == -1413891222336124627L)
                    {
                        int         sCount = 0;
                        Iterator    sIt = pk.getSignaturesOfType(PGPSignature.SUBKEY_BINDING);
                        while (sIt.hasNext())
                        {
                            int type = ((PGPSignature)sIt.next()).getSignatureType();
                            if (type != PGPSignature.SUBKEY_BINDING)
                            {
                                return new SimpleTestResult(false, getName() + ": failed to return correct signature type");
                            }
                            sCount++;
                        }
                        
                        if (sCount != 1)
                        {
                            return new SimpleTestResult(false, getName() + ": failed to find binding signature");
                        }
                    }
                    
                    pk.getSignatures();
                    
                    if (k.getKeyID() == -4049084404703773049L
                         || k.getKeyID() == -1413891222336124627L)
                    {
                        k.extractPrivateKey(sec2pass1, "BC");
                    }
                    else if (k.getKeyID() == -6498553574938125416L
                        || k.getKeyID() == 59034765524361024L)
                    {
                        k.extractPrivateKey(sec2pass2, "BC");
                    }
                }
                
                if (keyCount != 2)
                {
                    return new SimpleTestResult(false, getName() + ": wrong number of secret keys");
                }
            }
            
            if (count != 2)
            {
                return new SimpleTestResult(false, getName() + ": wrong number of secret keyrings");
            }
            
            return new SimpleTestResult(true, getName() + ": Okay");
        }
        catch (Exception e)
        {
            if (e instanceof PGPException)
            {
                if (((PGPException)e).getUnderlyingException() != null)
                {
                    ((PGPException)e).getUnderlyingException().printStackTrace();
                }
            }
            else
            {
                e.printStackTrace();
            }
            return new SimpleTestResult(false, getName() + ": exception - "+ e);
        }
    }
    
    public TestResult test3()
    {
        try
        {
            PGPPublicKeyRingCollection    pubRings = new PGPPublicKeyRingCollection(pub3);

            int                                        count = 0;

            byte[]    encRing = pubRings.getEncoded();

            pubRings = new PGPPublicKeyRingCollection(encRing);
            
            Iterator    rIt = pubRings.getKeyRings();
            
            while (rIt.hasNext())
            {
                PGPPublicKeyRing                    pgpPub = (PGPPublicKeyRing)rIt.next();
                
                count++;
                
                int    keyCount = 0;
                
                byte[]    bytes = pgpPub.getEncoded();
                
                pgpPub = new PGPPublicKeyRing(bytes);
                
                Iterator    it = pgpPub.getPublicKeys();
                while (it.hasNext())
                {
                    keyCount++;

                    PGPPublicKey pubK = (PGPPublicKey)it.next();
                    
                    pubK.getSignatures();
                }
                
                if (keyCount != 2)
                {
                    return new SimpleTestResult(false, getName() + ": wrong number of public keys");
                }
            }
            
            if (count != 1)
            {
                return new SimpleTestResult(false, getName() + ": wrong number of public keyrings");
            }
            
            PGPSecretKeyRingCollection    secretRings = new PGPSecretKeyRingCollection(sec3);

            rIt = secretRings.getKeyRings();
            count = 0;
            
            encRing = secretRings.getEncoded();
            
            secretRings = new PGPSecretKeyRingCollection(encRing);
            
            while (rIt.hasNext())
            {
                PGPSecretKeyRing                    pgpSec = (PGPSecretKeyRing)rIt.next();
        
                count++;
                
                int    keyCount = 0;
                
                byte[]    bytes = pgpSec.getEncoded();
                
                pgpSec = new PGPSecretKeyRing(bytes);
                
                Iterator    it = pgpSec.getSecretKeys();
                while (it.hasNext())
                {
                    keyCount++;

                    PGPSecretKey    k = (PGPSecretKey)it.next();

                    k.extractPrivateKey(sec3pass1, "BC");
                }
                
                if (keyCount != 2)
                {
                    return new SimpleTestResult(false, getName() + ": wrong number of secret keys");
                }
            }
            
            if (count != 1)
            {
                return new SimpleTestResult(false, getName() + ": wrong number of secret keyrings");
            }
            
            return new SimpleTestResult(true, getName() + ": Okay");
        }
        catch (Exception e)
        {
            if (e instanceof PGPException)
            {
                if (((PGPException)e).getUnderlyingException() != null)
                {
                    ((PGPException)e).getUnderlyingException().printStackTrace();
                }
            }
            return new SimpleTestResult(false, getName() + ": exception - " + e.toString());
        }
    }
    
    public TestResult test4()
    {
        try
        {
            PGPSecretKeyRingCollection    secretRings = new PGPSecretKeyRingCollection(sec4);

            Iterator    rIt = secretRings.getKeyRings();
            int            count = 0;
            
            byte[]    encRing = secretRings.getEncoded();
            
            secretRings = new PGPSecretKeyRingCollection(encRing);
            
            while (rIt.hasNext())
            {
                PGPSecretKeyRing                    pgpSec = (PGPSecretKeyRing)rIt.next();
        
                count++;
                
                int    keyCount = 0;
                
                byte[]    bytes = pgpSec.getEncoded();
                
                pgpSec = new PGPSecretKeyRing(bytes);
                
                Iterator    it = pgpSec.getSecretKeys();
                while (it.hasNext())
                {
                    keyCount++;

                    PGPSecretKey    k = (PGPSecretKey)it.next();

                    k.extractPrivateKey(sec3pass1, "BC");
                }
                
                if (keyCount != 2)
                {
                    return new SimpleTestResult(false, getName() + ": wrong number of secret keys");
                }
            }
            
            if (count != 1)
            {
                return new SimpleTestResult(false, getName() + ": wrong number of secret keyrings");
            }
            
            return new SimpleTestResult(true, getName() + ": Okay");
        }
        catch (Exception e)
        {
            if (e instanceof PGPException)
            {
                if (((PGPException)e).getUnderlyingException() != null)
                {
                    ((PGPException)e).getUnderlyingException().printStackTrace();
                }
            }
            return new SimpleTestResult(false, getName() + ": exception - " + e.toString());
        }
    }

    public TestResult test5()
    {
        try
        {
            PGPPublicKeyRingCollection    pubRings = new PGPPublicKeyRingCollection(pub5);

            int                                        count = 0;

            byte[]    encRing = pubRings.getEncoded();

            pubRings = new PGPPublicKeyRingCollection(encRing);
            
            Iterator    rIt = pubRings.getKeyRings();
            
            while (rIt.hasNext())
            {
                PGPPublicKeyRing                    pgpPub = (PGPPublicKeyRing)rIt.next();
                
                count++;
                
                int    keyCount = 0;
                
                byte[]    bytes = pgpPub.getEncoded();
                
                pgpPub = new PGPPublicKeyRing(bytes);
                
                Iterator    it = pgpPub.getPublicKeys();
                while (it.hasNext())
                {
                    keyCount++;

                    it.next();
                }
                
                if (keyCount != 2)
                {
                    return new SimpleTestResult(false, getName() + ": wrong number of public keys");
                }
            }
            
            if (count != 1)
            {
                return new SimpleTestResult(false, getName() + ": wrong number of public keyrings");
            }
            
            PGPSecretKeyRingCollection    secretRings = new PGPSecretKeyRingCollection(sec5);

            rIt = secretRings.getKeyRings();
            count = 0;
            
            encRing = secretRings.getEncoded();
            
            secretRings = new PGPSecretKeyRingCollection(encRing);
            
            while (rIt.hasNext())
            {
                PGPSecretKeyRing                    pgpSec = (PGPSecretKeyRing)rIt.next();
        
                count++;
                
                int    keyCount = 0;
                
                byte[]    bytes = pgpSec.getEncoded();
                
                pgpSec = new PGPSecretKeyRing(bytes);
                
                Iterator    it = pgpSec.getSecretKeys();
                while (it.hasNext())
                {
                    keyCount++;

                    PGPSecretKey    k = (PGPSecretKey)it.next();

                    k.extractPrivateKey(sec5pass1, "BC");
                }
                
                if (keyCount != 2)
                {
                    return new SimpleTestResult(false, getName() + ": wrong number of secret keys");
                }
            }
            
            if (count != 1)
            {
                return new SimpleTestResult(false, getName() + ": wrong number of secret keyrings");
            }
            
            return new SimpleTestResult(true, getName() + ": Okay");
        }
        catch (Exception e)
        {
            if (e instanceof PGPException)
            {
                if (((PGPException)e).getUnderlyingException() != null)
                {
                    ((PGPException)e).getUnderlyingException().printStackTrace();
                }
            }
            return new SimpleTestResult(false, getName() + ": exception - " + e.toString());
        }
    }
    
    public TestResult test6()
    {
        try
        {
            PGPPublicKeyRingCollection    pubRings = new PGPPublicKeyRingCollection(pub6);
            Iterator                    rIt = pubRings.getKeyRings();

            while (rIt.hasNext())
            {
                PGPPublicKeyRing    pgpPub = (PGPPublicKeyRing)rIt.next();
                Iterator            it = pgpPub.getPublicKeys();
                while (it.hasNext())
                {
                    PGPPublicKey    k = (PGPPublicKey)it.next();
                    
                    if (k.getKeyID() == 0x5ce086b5b5a18ff4L)
                    {
                        int             count = 0;
                        Iterator        sIt = k.getSignaturesOfType(PGPSignature.SUBKEY_REVOCATION);
                        while (sIt.hasNext())
                        {
                            PGPSignature sig = (PGPSignature)sIt.next();
                            count++;
                        }
                        
                        if (count != 1)
                        {
                            return new SimpleTestResult(false, getName() + ": wrong number of revocations in test6.");
                        }
                    }
                }
            }
            
            byte[]    encRing = pubRings.getEncoded();
            
            return new SimpleTestResult(true, getName() + ": Okay");
        }
        catch (Exception e)
        {
            if (e instanceof PGPException)
            {
                if (((PGPException)e).getUnderlyingException() != null)
                {
                    ((PGPException)e).getUnderlyingException().printStackTrace();
                }
            }
            e.printStackTrace();
            return new SimpleTestResult(false, getName() + ": exception - " + e.toString());
        }
    }

    public TestResult test7()
    {
        try
        {
            PGPPublicKeyRing    pgpPub = new PGPPublicKeyRing(pub7);
            Iterator            it = pgpPub.getPublicKeys();
            PGPPublicKey        masterKey = null;

            while (it.hasNext())
            {
                PGPPublicKey    k = (PGPPublicKey)it.next();

                if (k.isMasterKey())
                {
                    masterKey = k;
                    continue;
                }
                
                int             count = 0;
                PGPSignature    sig = null;
                Iterator        sIt = k.getSignaturesOfType(PGPSignature.SUBKEY_REVOCATION);

                while (sIt.hasNext())
                {
                    sig = (PGPSignature)sIt.next();
                    count++;
                }
                    
                if (count != 1)
                {
                    return new SimpleTestResult(false, getName() + ": wrong number of revocations in test7.");
                }

                sig.initVerify(masterKey, "BC");
                                                                                
                if (!sig.verifyCertification(k))
                {
                    return new SimpleTestResult(false, getName() + ": failed to verify revocation certification");
                }
            }
            
            return new SimpleTestResult(true, getName() + ": Okay");
        }
        catch (Exception e)
        {
            if (e instanceof PGPException)
            {
                if (((PGPException)e).getUnderlyingException() != null)
                {
                    ((PGPException)e).getUnderlyingException().printStackTrace();
                }
            }
            return new SimpleTestResult(false, getName() + ": exception - " + e.toString());
        }
    }

    public TestResult test8()
    {
        try
        {
            PGPPublicKeyRingCollection    pubRings = new PGPPublicKeyRingCollection(pub8);

            int                           count = 0;

            byte[]    encRing = pubRings.getEncoded();

            pubRings = new PGPPublicKeyRingCollection(encRing);
            
            Iterator    rIt = pubRings.getKeyRings();
            
            while (rIt.hasNext())
            {
                PGPPublicKeyRing          pgpPub = (PGPPublicKeyRing)rIt.next();
                
                count++;
                
                int    keyCount = 0;
                
                byte[]    bytes = pgpPub.getEncoded();
                
                pgpPub = new PGPPublicKeyRing(bytes);
                
                Iterator    it = pgpPub.getPublicKeys();
                while (it.hasNext())
                {
                    keyCount++;

                    it.next();
                }
                
                if (keyCount != 2)
                {
                    return new SimpleTestResult(false, getName() + ": wrong number of public keys");
                }
            }
            
            if (count != 2)
            {
                return new SimpleTestResult(false, getName() + ": wrong number of public keyrings");
            }
            
            PGPSecretKeyRingCollection    secretRings = new PGPSecretKeyRingCollection(sec8);

            rIt = secretRings.getKeyRings();
            count = 0;
            
            encRing = secretRings.getEncoded();
            
            secretRings = new PGPSecretKeyRingCollection(encRing);
            
            while (rIt.hasNext())
            {
                PGPSecretKeyRing         pgpSec = (PGPSecretKeyRing)rIt.next();
        
                count++;
                
                int    keyCount = 0;
                
                byte[]    bytes = pgpSec.getEncoded();
                
                pgpSec = new PGPSecretKeyRing(bytes);
                
                Iterator    it = pgpSec.getSecretKeys();
                while (it.hasNext())
                {
                    keyCount++;

                    PGPSecretKey    k = (PGPSecretKey)it.next();

                    k.extractPrivateKey(sec8pass, "BC");
                }
                
                if (keyCount != 2)
                {
                    return new SimpleTestResult(false, getName() + ": wrong number of secret keys");
                }
            }
            
            if (count != 1)
            {
                return new SimpleTestResult(false, getName() + ": wrong number of secret keyrings");
            }
            
            return new SimpleTestResult(true, getName() + ": Okay");
        }
        catch (Exception e)
        {
            if (e instanceof PGPException)
            {
                if (((PGPException)e).getUnderlyingException() != null)
                {
                    ((PGPException)e).getUnderlyingException().printStackTrace();
                }
            }
            return new SimpleTestResult(false, getName() + ": exception - " + e.toString());
        }
    }
    
    public TestResult test9()
    {
        try
        {   
            PGPSecretKeyRingCollection    secretRings = new PGPSecretKeyRingCollection(sec9);

            Iterator    rIt = secretRings.getKeyRings();
            int         count = 0;
            
            byte[] encRing = secretRings.getEncoded();
            
            secretRings = new PGPSecretKeyRingCollection(encRing);
            
            while (rIt.hasNext())
            {
                PGPSecretKeyRing         pgpSec = (PGPSecretKeyRing)rIt.next();
        
                count++;
                
                int    keyCount = 0;
                
                byte[]    bytes = pgpSec.getEncoded();
                
                pgpSec = new PGPSecretKeyRing(bytes);
                
                Iterator    it = pgpSec.getSecretKeys();
                while (it.hasNext())
                {
                    keyCount++;

                    PGPSecretKey    k = (PGPSecretKey)it.next();

                    PGPPrivateKey   pKey = k.extractPrivateKey(sec9pass, "BC");
                    if (keyCount == 1 && pKey != null)
                    {
                        return new SimpleTestResult(false, getName() + ": primary secret key found, null expected");
                    }
                }
                
                if (keyCount != 3)
                {
                    return new SimpleTestResult(false, getName() + ": wrong number of secret keys");
                }
            }
            
            if (count != 1)
            {
                return new SimpleTestResult(false, getName() + ": wrong number of secret keyrings");
            }
            
            return new SimpleTestResult(true, getName() + ": Okay");
        }
        catch (Exception e)
        {
            if (e instanceof PGPException)
            {
                if (((PGPException)e).getUnderlyingException() != null)
                {
                    ((PGPException)e).getUnderlyingException().printStackTrace();
                }
            }
            e.printStackTrace();
            return new SimpleTestResult(false, getName() + ": exception - " + e.toString());
        }
    }
    
    public TestResult generateTest()
    {
        try
        {
            char[]                    passPhrase = "hello".toCharArray();
            KeyPairGenerator    dsaKpg = KeyPairGenerator.getInstance("DSA", "BC");
        
            dsaKpg.initialize(512);
        
            //
            // this takes a while as the key generator has to generate some DSA params
            // before it generates the key.
            //
            KeyPair                    dsaKp = dsaKpg.generateKeyPair();
        
            KeyPairGenerator    elgKpg = KeyPairGenerator.getInstance("ELGAMAL", "BC");
            BigInteger             g = new BigInteger("153d5d6172adb43045b68ae8e1de1070b6137005686d29d3d73a7749199681ee5b212c9b96bfdcfa5b20cd5e3fd2044895d609cf9b410b7a0f12ca1cb9a428cc", 16);
            BigInteger             p = new BigInteger("9494fec095f3b85ee286542b3836fc81a5dd0a0349b4c239dd38744d488cf8e31db8bcb7d33b41abb9e5a33cca9144b1cef332c94bf0573bf047a3aca98cdf3b", 16);
            
            ElGamalParameterSpec         elParams = new ElGamalParameterSpec(p, g);
            
            elgKpg.initialize(elParams);
        
            //
            // this is quicker because we are using pregenerated parameters.
            //
            KeyPair                    elgKp = elgKpg.generateKeyPair();
            PGPKeyPair        dsaKeyPair = new PGPKeyPair(PGPPublicKey.DSA, dsaKp, new Date(), "BC");
            PGPKeyPair        elgKeyPair = new PGPKeyPair(PGPPublicKey.ELGAMAL_ENCRYPT, elgKp, new Date(), "BC");
        
            PGPKeyRingGenerator    keyRingGen = new PGPKeyRingGenerator(PGPSignature.POSITIVE_CERTIFICATION, dsaKeyPair,
                    "test", PGPEncryptedData.AES_256, passPhrase, null, null, new SecureRandom(), "BC");
        
            keyRingGen.addSubKey(elgKeyPair);
        
            PGPSecretKeyRing    keyRing = keyRingGen.generateSecretKeyRing();
            
            PGPPublicKeyRing        pubRing = keyRingGen.generatePublicKeyRing();
            
            PGPPublicKey            vKey = null;
            PGPPublicKey            sKey = null;
            
            Iterator                    it = pubRing.getPublicKeys();
            while (it.hasNext())
            {
                PGPPublicKey    pk = (PGPPublicKey)it.next();
                if (pk.isMasterKey())
                {
                    vKey = pk;
                }
                else
                {
                    sKey = pk;
                }
            }
            
            Iterator    sIt = sKey.getSignatures();
            while (sIt.hasNext())
            {
                PGPSignature    sig = (PGPSignature)sIt.next();
                
                if (sig.getKeyID() == vKey.getKeyID()
                    && sig.getSignatureType() == PGPSignature.SUBKEY_BINDING)
                {
                    sig.initVerify(vKey, "BC");

                    if (!sig.verifyCertification(vKey, sKey))
                    {
                        return new SimpleTestResult(false, getName() + ": failed to verify sub-key signature.");
                    }
                }
            }
            return new SimpleTestResult(true, getName() + ": Okay");
        }
        catch (Exception e)
        {
            if (e instanceof PGPException)
            {
                if (((PGPException)e).getUnderlyingException() != null)
                {
                    ((PGPException)e).getUnderlyingException().printStackTrace();
                }
            }
            return new SimpleTestResult(false, getName() + ": exception - " + e.toString());
        }
    }
    
    public TestResult perform()
    {
        TestResult    res = test1();
        if (!res.isSuccessful())
        {
            return res;
        }
 
        res = test2();
        if (!res.isSuccessful())
        {
            return res;
        }
 
        res = test3();
        if (!res.isSuccessful())
        {
            return res;
        }
        
        res = test4();
        if (!res.isSuccessful())
        {
            return res;
        }

        res = test5();
        if (!res.isSuccessful())
        {
            return res;
        }
        
        res = test6();
        if (!res.isSuccessful())
        {
            return res;
        }

//        res = test7();
//        if (!res.isSuccessful())
//        {
//            return res;
//        }
        
        res = test8();
        if (!res.isSuccessful())
        {
            return res;
        }
        
        res = test9();
        if (!res.isSuccessful())
        {
            return res;
        }
        
        return generateTest();
    }

    public String getName()
    {
        return "PGPKeyRingTest";
    }

    public static void main(
        String[]    args)
    {
        Test            test = new PGPKeyRingTest();
        TestResult      result = test.perform();

        System.out.println(result.toString());
    }
}