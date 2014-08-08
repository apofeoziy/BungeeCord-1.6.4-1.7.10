package net.md_5.bungee.protocol.packet.protocolhack;

import io.netty.buffer.ByteBuf;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.md_5.bungee.protocol.packet.AbstractPacketHandler;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class PacketEncryptionResponse extends Defined172Packet {

    byte[] sharedSecret;
    byte[] verifyToken;

    public PacketEncryptionResponse() {
        super( 0x01 );
    }

    @Override
    public void read(ByteBuf buf) {
        sharedSecret = readArray( buf );
        verifyToken = readArray( buf );
    }

    @Override
    public void write(ByteBuf buf) {
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle( this );
    }

}
