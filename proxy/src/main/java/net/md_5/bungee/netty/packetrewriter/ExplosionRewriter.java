package net.md_5.bungee.netty.packetrewriter;

import io.netty.buffer.ByteBuf;

public class ExplosionRewriter extends PacketRewriter
{

    @Override
    public void rewriteClientToServer(ByteBuf in, ByteBuf out)
    {
        unsupported( true );
    }

    @Override
    public void rewriteServerToClient(ByteBuf in, ByteBuf out)
    {
        float x = (float) in.readDouble();
        float y = (float) in.readDouble();
        float z = (float) in.readDouble();
        out.writeFloat( x );
        out.writeFloat( y );
        out.writeFloat( z );
        out.writeBytes( in.readBytes( in.readableBytes() ) );
    }
}
