package net.md_5.bungee.protocol.packet.protocolhack;

import io.netty.buffer.ByteBuf;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.md_5.bungee.protocol.packet.AbstractPacketHandler;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class PacketPing extends Defined172Packet {

    long ping;

    public PacketPing() {
        super( 0x01 );
    }

    @Override
    public void read(ByteBuf buf) {
        ping = buf.readLong();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeLong( ping );
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle( this );
    }

}
