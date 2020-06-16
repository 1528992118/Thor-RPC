package org.cxl.thor.rpc.serialize.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.cxl.thor.rpc.serialize.Serializer;

/**
 * @author cxl
 * @Description: Netty编码器
 * @date 2020/6/10 14:17
 */
public class NettyEncoder extends MessageToByteEncoder {

    private Serializer serializer;

    public NettyEncoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        byte[] data = (byte[]) serializer.serialize(msg);
        out.writeInt(data.length);
        out.writeBytes(data);
    }

}
