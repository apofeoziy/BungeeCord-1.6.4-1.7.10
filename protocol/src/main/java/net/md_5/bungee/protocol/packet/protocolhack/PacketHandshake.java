package net.md_5.bungee.protocol.packet.protocolhack;

import io.netty.buffer.ByteBuf;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.md_5.bungee.protocol.packet.AbstractPacketHandler;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class PacketHandshake extends Defined172Packet {

    int protocolVersion;
    String serverAddress;
    int serverPort;
    int state;

    public PacketHandshake() {
        super( 0x02 );
    }

    @Override
    public void read(ByteBuf buf) {
        protocolVersion = readVarInt( buf );
        serverAddress = readString( buf, true );
        serverPort = buf.readUnsignedShort();
        state = readVarInt( buf );
    }

    @Override
    public void write(ByteBuf buf) {
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle( this );
    }
}
