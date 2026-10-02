import React, { useState } from 'react';
import api from '../api/client';
import { Bot, Send, Sparkles, ShieldAlert, CheckCircle2 } from 'lucide-react';

const AiAssistant: React.FC = () => {
  const [prompt, setPrompt] = useState('');
  const [loading, setLoading] = useState(false);
  const [messages, setMessages] = useState<{ sender: 'user' | 'ai'; text: string; disclaimer?: string }[]>([
    {
      sender: 'ai',
      text: 'Hello! I am your MediCore Operations & Clinical Workflow Assistant. You can ask me how FEFO pharmacy dispensing works, how appointment conflict locking is implemented, or how bed billing is computed.',
    },
  ]);

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!prompt.trim() || loading) return;

    const userMessage = prompt.trim();
    setMessages((prev) => [...prev, { sender: 'user', text: userMessage }]);
    setPrompt('');
    setLoading(true);

    try {
      const res = await api.post('/hospital/ai/ask', { prompt: userMessage });
      setMessages((prev) => [
        ...prev,
        { sender: 'ai', text: res.data.answer, disclaimer: res.data.disclaimer },
      ]);
    } catch (err: any) {
      setMessages((prev) => [
        ...prev,
        { sender: 'ai', text: 'Sorry, I encountered an error connecting to the backend assistant service.' },
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-800 flex items-center space-x-2">
            <Bot className="w-6 h-6 text-blue-600" />
            <span>AI Operations & Workflow Assistant</span>
          </h1>
          <p className="text-sm text-slate-500">
            Intelligent assistant for administrative guidance, inventory insights, and system policies
          </p>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 shadow-sm flex flex-col h-[520px]">
        {/* Chat History */}
        <div className="flex-1 overflow-y-auto p-4 space-y-4">
          {messages.map((m, idx) => (
            <div
              key={idx}
              className={`flex ${m.sender === 'user' ? 'justify-end' : 'justify-start'}`}
            >
              <div
                className={`max-w-[80%] rounded-2xl px-4 py-3 text-xs leading-relaxed ${
                  m.sender === 'user'
                    ? 'bg-blue-600 text-white rounded-br-none'
                    : 'bg-slate-100 text-slate-800 rounded-bl-none border border-slate-200/60'
                }`}
              >
                <p className="whitespace-pre-wrap">{m.text}</p>
                {m.disclaimer && (
                  <p className="mt-2 pt-2 border-t border-slate-200 text-[10px] text-slate-500 italic">
                    ⚠️ {m.disclaimer}
                  </p>
                )}
              </div>
            </div>
          ))}
          {loading && (
            <div className="flex justify-start">
              <div className="bg-slate-100 text-slate-500 rounded-2xl px-4 py-3 text-xs rounded-bl-none animate-pulse flex items-center space-x-2">
                <Sparkles className="w-3.5 h-3.5 text-blue-600" />
                <span>MediCore Assistant is typing...</span>
              </div>
            </div>
          )}
        </div>

        {/* Input Bar */}
        <div className="p-3 border-t border-slate-200 bg-slate-50 rounded-b-xl">
          <form onSubmit={handleSend} className="flex space-x-2">
            <input
              type="text"
              value={prompt}
              onChange={(e) => setPrompt(e.target.value)}
              placeholder="Ask about FEFO batch allocation, appointment scheduling rules, billing calculations..."
              className="flex-1 px-3 py-2 text-xs rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
            />
            <button
              type="submit"
              disabled={loading || !prompt.trim()}
              className="px-4 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-slate-300 text-white rounded-lg text-xs font-bold flex items-center space-x-1 transition-colors"
            >
              <Send className="w-3.5 h-3.5" />
              <span>Send</span>
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AiAssistant;
