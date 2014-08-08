package net.md_5.bungee.protocol.packet.protocolhack;

import io.netty.buffer.ByteBuf;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.md_5.bungee.protocol.packet.AbstractPacketHandler;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class PacketPingResponse extends Defined172Packet {

    String response;

    PacketPingResponse() {
        super( 0x00 );
    }

    public PacketPingResponse(String response) {
        this();
        this.response = response;
    }

    @Override
    public void read(ByteBuf buf) {
    }

    @Override
    public void write(ByteBuf buf) {
        writeString( response, buf, true );
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        // Meh
    }
}
