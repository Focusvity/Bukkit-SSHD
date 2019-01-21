package me.focusvity.bukkitssh.authenticator;

import com.ryanmichela.bukkitssh.util.PemDecoder;
import me.focusvity.bukkitssh.BukkitSSH;
import org.apache.commons.lang.ArrayUtils;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.io.File;
import java.io.FileReader;
import java.security.PublicKey;
import java.util.logging.Level;

public class PublicKeyAuthenticator implements PublickeyAuthenticator
{

    @Override
    public boolean authenticate(String username, PublicKey key, ServerSession serverSession) throws AsyncAuthException
    {
        File keyFile = new File(BukkitSSH.instance.keys, username);

        if (keyFile.exists())
        {
            try
            {
                FileReader reader = new FileReader(keyFile);
                PemDecoder decoder = new PemDecoder(reader);
                PublicKey pkey = decoder.getPemBytes();
                reader.close();

                if (key != null)
                {
                    return ArrayUtils.isEquals(key.getEncoded(), pkey.getEncoded());
                }
                else
                {
                    BukkitSSH.instance.getLogger().log(Level.SEVERE, "Could not parse PEM file: "
                            + keyFile.getAbsolutePath());
                }
            }
            catch (Exception e)
            {
                BukkitSSH.instance.getLogger().log(Level.SEVERE, "Could not process a public key: "
                        + keyFile.getAbsolutePath(), e);
            }
        }
        else
        {
            BukkitSSH.instance.getLogger().log(Level.WARNING, "Could not locate a public key for " + username);
        }

        return false;
    }
}
