package org.starloco.locos.object;

import org.starloco.locos.client.other.Stats;
import org.starloco.locos.util.Pair;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ItemHash makes it easier to find similar items(same template, same stats).
 * It can be used as a map key.
 * DO NOT STORE IN DATABASE. We may want to change how we compute the hashes later on.
 * */
public class ItemHash {

    private static final MessageDigest digest;
    private static final Charset charset = StandardCharsets.UTF_8;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public final int templateId;
    public final String strStats;
    public final String hash;

    public ItemHash(GameObject item) {
        this.templateId = item.getTemplate().getId();
        this.strStats = item.encodeStats();
        this.hash = hash(this.templateId, item.getStats(), item.getTxtStat());
    }

    private static String hash(int templateID, Stats stats, Map<Integer, String> txtStats) {
        // Sort stats in effectID order, we need determinism
        List<Pair<Integer, Integer>> sortedStats = stats.getEffects().entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(e -> new Pair<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        // Sort txtStats in effectID order, we need determinism
        // TODO: Maybe we need to also sort the txt stats values ?
        List<Pair<Integer, String>> sortedTxtStats = txtStats.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(e -> new Pair<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        byte[] message;
        try(ByteArrayOutputStream bbos = new ByteArrayOutputStream()) {
            try(DataOutputStream dos = new DataOutputStream(bbos)){
                dos.writeInt(templateID);

                for(Pair<Integer,Integer> p : sortedStats) {
                    dos.writeInt(p.first);
                    dos.writeInt(p.second);
                }

                for(Pair<Integer, String> p : sortedTxtStats) {
                    dos.writeInt(p.first);
                    dos.write(p.second.getBytes(charset));
                }
            }
            message = bbos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // digest is not thread-safe :(
        synchronized (digest) {
            return DatatypeConverter.printHexBinary(digest.digest(message));
        }
    }

    public int hashCode() {
        return this.hash.hashCode();
    }

    public boolean equals(Object o) {
        if(!(o instanceof ItemHash)) return false;
        return Objects.equals(((ItemHash) o).hash, this.hash);
    }
}
