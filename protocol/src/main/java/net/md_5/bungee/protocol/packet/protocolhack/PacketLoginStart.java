package net.md_5.bungee.protocol.packet.protocolhack;

import io.netty.buffer.ByteBuf;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.md_5.bungee.protocol.packet.AbstractPacketHandler;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class PacketLoginStart extends Defined172Packet {

    String user;

    public PacketLoginStart() {
        super( 0x00 );
    }

    @Override
    public void read(ByteBuf buf) {
        user = readString( buf, true );
    }

    @Override
    public void write(ByteBuf buf) {
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle( this );
    }

}
