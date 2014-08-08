package net.md_5.bungee.netty.packetrewriter;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.netty.PacketMapping;
import net.md_5.bungee.netty.Var;

public class AnimationRewriter extends PacketRewriter
{
    @Override
    public void rewriteClientToServer(ByteBuf in, ByteBuf out)
    {
        out.writeBytes( in.readBytes( in.readableBytes() ) );
    }

    @Override
    public void rewriteServerToClient(ByteBuf in, ByteBuf out)
    {
        int entityId = in.readInt();
        byte animation = in.readByte();
        Var.writeVarInt( entityId, out );
        out.writeByte( PacketMapping.animations[ animation ] );
    }
}
