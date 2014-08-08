package net.md_5.bungee.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class YamlConfiguration extends ConfigurationProvider
{

    private final ThreadLocal<Yaml> yaml = new ThreadLocal<Yaml>()
    {
        @Override
        protected Yaml initialValue()
        {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle( DumperOptions.FlowStyle.BLOCK );
            return new Yaml( options );
        }
    };

    @Override
    public void save(Configuration config, File file) throws IOException
    {
        try ( FileWriter writer = new FileWriter( file ) )
        {
            save( config, writer );
        }
    }

    @Override
    public void save(Configuration config, Writer writer)
    {
        yaml.get().dump( config.self, writer );
    }

    @Override
    public Configuration load(File file) throws IOException
    {
        try ( FileReader reader = new FileReader( file ) )
        {
            return load( reader );
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Configuration load(Reader reader)
    {
        Configuration conf = new Configuration( (Map<String, Object>) yaml.get().loadAs( reader, Map.class ), null );
        return conf;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Configuration load(String string)
    {
        Configuration conf = new Configuration( (Map<String, Object>) yaml.get().loadAs( string, Map.class ), null );
        return conf;
    }
}
