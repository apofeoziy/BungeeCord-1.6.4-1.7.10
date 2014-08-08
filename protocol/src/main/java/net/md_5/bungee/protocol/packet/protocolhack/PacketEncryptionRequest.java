package net.md_5.bungee.protocol.packet.protocolhack;

import io.netty.buffer.ByteBuf;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.md_5.bungee.protocol.packet.AbstractPacketHandler;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class PacketEncryptionRequest extends Defined172Packet
{

    private String serverId;
    private byte[] publicKey;
    private byte[] verifyToken;

    private PacketEncryptionRequest()
    {
        super( 0x01 );
    }

    public PacketEncryptionRequest(String serverId, byte[] publicKey, byte[] verifyToken)
    {
        this();
        this.serverId = serverId;
        this.publicKey = publicKey;
        this.verifyToken = verifyToken;
    }

    @Override
    public void read(ByteBuf buf)
    {
        serverId = readString( buf, true );
        publicKey = readArray( buf );
        verifyToken = readArray( buf );
    }

    @Override
    public void write(ByteBuf buf)
    {
        writeString( serverId, buf, true );
        writeArray( publicKey, buf );
        writeArray( verifyToken, buf );
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
    }
}
