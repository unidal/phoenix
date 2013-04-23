require 'citrus'
require 'pp'
require 'set'
require 'stringio'

def parse_f5(stringio, default_pool)
  Citrus.load 'f5'
  doc = File.read('/Users/marsqing/Downloads/WEB.MAIN.txt')
  f5 = F5.parse(doc, :root => :whens)

  target_pool_set = Set.new
  f5_rule_str = StringIO.new
  pool_str = StringIO.new

  f5.value.zip(f5.pool_targets).each do |patterns, target_pool|
    if patterns && patterns[0] && /^HTTP.*/!~target_pool && /^\d.*/!~target_pool
      target_pool_set.add target_pool
      f5_rule_str << "<f5-rule target-pool='#{target_pool}'>\n"
      patterns.each do |p|
        p = p.gsub(/\\\\/, "\\")
        f5_rule_str << "\t<pattern>#{p}</pattern>\n"
      end
      f5_rule_str << "</f5-rule>\n"
    end
  end

  stringio << "<pool name='Default' url-pattern='http://#{default_pool}%s' />\n"

  target_pool_set.each { |p| pool_str << "<!--pool name='#{p}' url-pattern='http://HOST:PORT/CONTEXT%s' /-->\n" }

  stringio << pool_str.string
  stringio << f5_rule_str.string
end
